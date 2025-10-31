package br.com.fiap.wtcclienteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import br.com.fiap.wtcclienteapp.network.model.MensagemResponse
import kotlinx.coroutines.launch

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val peerName = intent.getStringExtra(EXTRA_PEER_NAME) ?: "Atendente"
        val conversaId = intent.getLongExtra(EXTRA_CONVERSA_ID, 0)
        setContent {
            WTCAppTheme {
                ChatScreen(peerName, conversaId)
            }
        }
    }

    companion object {
        const val EXTRA_PEER_ID = "peer_id"
        const val EXTRA_PEER_NAME = "peer_name"
        const val EXTRA_CONVERSA_ID = "conversa_id"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(peerName: String, conversaId: Long) {
    val viewModel: MensagemViewModel = viewModel()
    var input by remember { mutableStateOf("") }
    val messagesState by viewModel.mensagens.observeAsState(emptyList())
    val erro by viewModel.erro.observeAsState(null)
    val sucesso by viewModel.sucesso.observeAsState(null)
    val scope = rememberCoroutineScope()
    
    // Mostrar mensagens de erro/sucesso
    LaunchedEffect(erro) {
        erro?.let {
            // Toast poderia ser usado aqui se necessário
        }
    }
    
    LaunchedEffect(sucesso) {
        sucesso?.let {
            viewModel.limparMensagens()
        }
    }
    
    // Carregar mensagens ao iniciar
    LaunchedEffect(conversaId) {
        val idParaUsar = if (conversaId > 0) conversaId else 1L // Se não houver conversaId, usar 1 como default (mock)
        viewModel.listarMensagens(idParaUsar)
    }
    
    // Recarregar mensagens após envio bem-sucedido
    LaunchedEffect(sucesso) {
        if (sucesso != null) {
            kotlinx.coroutines.delay(500) // Pequeno delay para garantir que a mensagem foi salva
            val idParaUsar = if (conversaId > 0) conversaId else 1L
            viewModel.listarMensagens(idParaUsar)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Chat com $peerName") }) },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Mensagem...") }
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            val mensagem = input.trim()
                            val idParaUsar = if (conversaId > 0) conversaId else 1L // Se não houver conversaId, usar 1 como default (mock)
                            input = ""
                            scope.launch {
                                viewModel.enviarMensagem(idParaUsar, mensagem)
                            }
                        }
                    },
                    enabled = input.isNotBlank() // Permitir enviar mesmo sem conversaId (usará mock)
                ) {
                    Icon(Icons.Filled.Send, contentDescription = "Enviar")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp),
            reverseLayout = true
        ) {
            if (messagesState.isEmpty()) {
                item {
                    Text("Nenhuma mensagem ainda. Inicie a conversa!")
                }
            } else {
                items(messagesState.reversed()) { msg ->
                    val isOperador = msg.remetenteNome?.contains("Operador", ignoreCase = true) == true ||
                                    msg.remetenteNome?.contains("OPERADOR", ignoreCase = true) == true
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = if (isOperador) Arrangement.End else Arrangement.Start
                    ) {
                        Column(
                            modifier = Modifier.widthIn(max = 280.dp),
                            horizontalAlignment = if (isOperador) Alignment.End else Alignment.Start
                        ) {
                            if (!isOperador) {
                                Text(
                                    text = msg.remetenteNome ?: "Desconhecido",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Card(
                                modifier = Modifier.padding(vertical = 2.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isOperador) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = msg.conteudo ?: "",
                                    modifier = Modifier.padding(12.dp),
                                    color = if (isOperador) 
                                        MaterialTheme.colorScheme.onPrimary 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (msg.dataEnvio != null) {
                                Text(
                                    text = formatarData(msg.dataEnvio),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatarData(dataEnvio: String): String {
    return try {
        // Formatar data de forma simples (ex: "2025-10-31T01:25:42" -> "31/10 01:25")
        val partes = dataEnvio.split("T")
        if (partes.size >= 2) {
            val data = partes[0].split("-").reversed().joinToString("/")
            val hora = partes[1].substringBefore(".").substring(0, 5)
            "$data $hora"
        } else {
            dataEnvio.take(16)
        }
    } catch (e: Exception) {
        dataEnvio.take(16)
    }
}


