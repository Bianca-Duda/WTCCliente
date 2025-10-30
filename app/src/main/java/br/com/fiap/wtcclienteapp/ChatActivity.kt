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

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val peerName = intent.getStringExtra(EXTRA_PEER_NAME) ?: "Atendente"
        setContent {
            WTCAppTheme {
                ChatScreen(peerName)
            }
        }
    }

    companion object {
        const val EXTRA_PEER_ID = "peer_id"
        const val EXTRA_PEER_NAME = "peer_name"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(peerName: String) {
    var input by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf("Conversa com $peerName iniciada.") }

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
                IconButton(onClick = {
                    if (input.isNotBlank()) {
                        messages.add(0, input)
                        input = ""
                    }
                }) {
                    Icon(Icons.Filled.Send, contentDescription = "Enviar")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp),
            reverseLayout = true
        ) {
            items(messages) { msg -> Text(msg) }
        }
    }
}


