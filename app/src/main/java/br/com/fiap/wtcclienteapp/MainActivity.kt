package br.com.fiap.wtcclienteapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi // IMPORT ADICIONADO para FlowRow
import androidx.compose.foundation.layout.FlowRow // IMPORT ADICIONADO para FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState // IMPORT ADICIONADO
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Mock de classes/funções do Firebase para satisfazer o requisito de conectividade
// Em um aplicativo real, isso seria substituído por dependências e inicializações reais.
object FirebaseMock {
    fun initialize() {
        Log.d("WTCApp", "Firebase inicializado (Mock).")
    }

    // Mock de função que registraria um listener em tempo real (ex: Firestore)
    fun setupRealtimeListener(onNewMessage: (Message) -> Unit) {
        // Simulação de mensagem recebida em tempo real após 5 segundos
        // Em um app real, onSnapshot ou um serviço de push chamaria onNewMessage.
        Log.d("WTCApp", "Listener de mensagens em tempo real configurado.")
    }

    // Mock para enviar token de push (ex: FCM)
    fun registerForPushNotifications(token: String) {
        Log.d("WTCApp", "Token FCM registrado: $token")
    }
}

// --- Data Models ---

// Modelo de ação interativa (botões ou links)
data class Action(
    val label: String,
    val type: ActionType,
    val value: String // Ex: URL, Deeplink, Comando
)

enum class ActionType {
    BUTTON_LINK,       // Botão que abre URL/Deeplink
    BUTTON_COMMAND,    // Botão que dispara um comando no CRM
    DEEPLINK_TEXT,     // Link de Deeplink embutido no texto
}

// Modelo de mensagem, suportando diferentes tipos de conteúdo
data class Message(
    val id: String,
    val sender: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: MessageType,
    val actions: List<Action> = emptyList(),
    val isImportant: Boolean = false,
    val isRead: Boolean = true
)

enum class MessageType {
    TEXT,
    CAMPAIGN, // Mensagem rica (promoção, banner, evento)
    SYSTEM    // Mensagens automáticas do sistema
}

// --- Main Activity ---

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMock.initialize() // Inicialização mockada
        setContent {
            WTCAppTheme {
                WTCClientApp()
            }
        }
    }
}

// --- Theme (Minimal) ---

@Composable
fun WTCAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF005691), // Azul WTC
            onPrimary = Color.White,
            surface = Color(0xFFF0F4F8), // Fundo claro
            onSurface = Color(0xFF1E293B)
        ),
        shapes = Shapes(
            extraSmall = RoundedCornerShape(4.dp),
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(16.dp)
        ),
        content = content
    )
}

// --- Application Composable ---

@OptIn(ExperimentalMaterial3Api::class) // Adicionado para TopAppBar
@Composable
fun WTCClientApp() {
    val initialMessages = remember {
        mutableStateListOf(
            Message(
                id = "1",
                sender = "CRM WTC",
                content = "Olá, [Nome do Cliente]! Seja bem-vindo à nossa nova experiência de comunicação. Recebemos seu pedido #4590. Use o comando /suporte para falar com um agente.",
                type = MessageType.SYSTEM
            ),
            Message(
                id = "2",
                sender = "Time de Marketing",
                content = "CAMPANHA EXCLUSIVA: Sua conta Premium foi ativada com 30% de desconto no primeiro mês. Clique abaixo para ver os termos.",
                type = MessageType.CAMPAIGN,
                actions = listOf(
                    Action("Ver Termos", ActionType.BUTTON_LINK, "https://wtc.com/terms"),
                    Action("Ativar Agora", ActionType.BUTTON_COMMAND, "/ativar_premium")
                ),
                isRead = false // Mensagem não lida
            ),
            Message(
                id = "3",
                sender = "CRM WTC",
                content = "Seu boleto de Outubro já está disponível. Acesse o link para pagamento imediato: [Deeplink Boleto]",
                type = MessageType.TEXT,
                actions = listOf(
                    Action("[Deeplink Boleto]", ActionType.DEEPLINK_TEXT, "wtcapp://fatura/outubro")
                ),
                isRead = false
            )
        )
    }

    // State para simular a nova mensagem (Push Notification)
    var showPopup by remember { mutableStateOf(false) }

    // Mock de escuta de mensagens em tempo real e simulação
    LaunchedEffect(Unit) {
        FirebaseMock.setupRealtimeListener { newMessage ->
            // Adiciona a nova mensagem ao topo
            initialMessages.add(0, newMessage)
            // Mostra o pop-up
            showPopup = true
        }

        // Simulação de recebimento de nova mensagem após 5 segundos
        delay(5000)
        val newMsg = Message(
            id = "4",
            sender = "Time de Eventos",
            content = "CONVITE: Temos um evento exclusivo para você em São Paulo! Confirme sua presença para garantir seu lugar.",
            type = MessageType.CAMPAIGN,
            actions = listOf(
                Action("Confirmar", ActionType.BUTTON_COMMAND, "/confirmar_evento_sp")
            ),
            isRead = false
        )
        initialMessages.add(0, newMsg)
        showPopup = true
        delay(4000)
        showPopup = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WTC Mensagens", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary),
                actions = {
                    IconButton(onClick = { /* Ação de busca */ }) {
                        Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    IconButton(onClick = { /* Ação de perfil */ }) {
                        Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                MessageList(initialMessages)

                // Popup de notificação in-app (push simulado)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    AnimatedVisibility(
                        visible = showPopup,
                        enter = fadeIn(animationSpec = tween(500)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        InAppNotification(message = initialMessages.first())
                    }
                }

                // Indicador de mensagens não lidas
                val unreadCount = initialMessages.count { !it.isRead }
                if (unreadCount > 0) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.error)
                            .clickable {
                                // Ação para rolar até a primeira não lida (simulado)
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Você tem $unreadCount mensagens não lidas", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }
    )
}

// --- Listagem do Histórico de Chat ---

@Composable
fun MessageList(messages: MutableList<Message>) {
    // Para obter o LazyListState e controlar a rolagem
    val listState = rememberLazyListState() // Correção do Unresolved reference

    // Efeito para rolar até a nova mensagem quando o total de itens mudar
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(0) // Rola para o topo (a mensagem mais nova)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        reverseLayout = true, // Para simular um chat (mensagens novas no topo, mas rola para baixo)
        state = listState // Aplica o estado à LazyColumn
    ) {
        itemsIndexed(messages, key = { _, msg -> msg.id }) { index, message ->
            // Lógica de Gestos Inteligentes (Long Press para criar Tarefa)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .pointerInput(message.id) {
                        detectTapGestures(
                            onLongPress = {
                                // Ex: Mostrar menu contextual para criar tarefa
                                Log.d("WTCApp", "Tarefa rápida criada para a mensagem (Long Press): ${message.id}")
                            }
                        )
                    }
            ) {
                // Conteúdo da mensagem
                RichMessageCard(message)

                // Área para mostrar ação de importante (se o status isImportant for true)
                if (message.isImportant) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Importante",
                        tint = Color(0xFFF5C71A), // Amarelo
                        modifier = Modifier.padding(start = 8.dp).size(24.dp).align(Alignment.CenterVertically)
                    )
                }
            }
            // Separador (se não for a última mensagem)
            if (index < messages.size - 1) {
                Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

// --- Renderização de Mensagem Rica (Individual) ---

@OptIn(ExperimentalLayoutApi::class) // Adicionado para FlowRow com mainAxisSpacing/crossAxisSpacing
@Composable
fun RichMessageCard(message: Message) {
    val context = LocalContext.current

    val backgroundColor = when {
        message.type == MessageType.CAMPAIGN -> Color(0xFFE5F6FF) // Azul claro para Campanhas
        message.type == MessageType.SYSTEM -> Color(0xFFFFFBE5) // Amarelo claro para Sistema
        else -> Color.White
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Cabeçalho (Remetente e Data)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = message.sender,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!message.isRead) {
                        // FiberManualRecord foi corrigido com o import Icons.Filled.*
                        Icon(Icons.Filled.FiberManualRecord, contentDescription = "Não Lido", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(10.dp))
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(
                        text = "Hoje, ${java.text.SimpleDateFormat("HH:mm").format(message.timestamp)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Conteúdo da Mensagem (Texto com tratamento de Deeplinks)
            Text(
                text = message.content,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            // Renderiza Deeplinks embutidos no texto
            message.actions.filter { it.type == ActionType.DEEPLINK_TEXT }.forEach { action ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = action.label,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable {
                            Log.d("WTCApp", "Deeplink acionado: ${action.value}")
                            // Simula Deep Link (ex: wtcapp://fatura/outubro)
                        }
                        .padding(vertical = 4.dp)
                )
            }

            // Renderiza Botões de Ação
            if (message.actions.any { it.type == ActionType.BUTTON_LINK || it.type == ActionType.BUTTON_COMMAND }) {
                Spacer(modifier = Modifier.height(12.dp))
                // FlowRow corrigido com @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    message.actions.filter { it.type == ActionType.BUTTON_LINK || it.type == ActionType.BUTTON_COMMAND }
                        .forEach { action ->
                            Button(
                                onClick = {
                                    val logMessage = when (action.type) {
                                        ActionType.BUTTON_LINK -> "Link/Deeplink: ${action.value}"
                                        ActionType.BUTTON_COMMAND -> "Comando CRM disparado: ${action.value}"
                                        else -> "Ação desconhecida"
                                    }
                                    Log.d("WTCApp", logMessage)
                                    // Em um app real: Abrir Browser ou Disparar API para o CRM/Backend
                                },
                                shape = MaterialTheme.shapes.small,
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(action.label, fontSize = 14.sp)
                            }
                        }
                }
            }
        }
    }
}

// --- Popup de Notificação In-App ---

@Composable
fun InAppNotification(message: Message) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .clickable {
                    // Clicar no popup leva o usuário para a mensagem no chat
                    Log.d("WTCApp", "Popup clicado: Rolar para a mensagem ${message.id}")
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Message,
                contentDescription = "Nova Mensagem",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Nova Mensagem de ${message.sender}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = message.content.take(60) + "...",
                    fontSize = 13.sp,
                    maxLines = 1,
                    color = Color.Gray
                )
            }
        }
    }
}

// --- Preview ---

@Preview(showBackground = true)
@Composable
fun PreviewWTCClientApp() {
    WTCAppTheme {
        WTCClientApp()
    }
}