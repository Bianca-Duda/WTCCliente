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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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

    // Simula o envio da mensagem do usuário para o backend
    fun sendMessageToBackend(message: Message) {
        Log.i("WTCApp", "MENSAGEM DO USUÁRIO ENVIADA: ${message.content}")
        // Em um app real: Aqui faria uma chamada para a API do CRM ou Firestore
    }

    // Simula o registro de interação (Ex: Curtida)
    fun logInteraction(messageId: String, interaction: String) {
        Log.i("WTCApp", "INTERAÇÃO: Mensagem $messageId -> $interaction")
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
    val isRead: Boolean = true,
    // NOVOS CAMPOS PARA INTERAÇÃO EM POSTS
    val isLiked: Boolean = false, // Se o usuário deu "Gostei"
    val likeCount: Int = 0        // Contador de "Gostei" (simulado)
)

enum class SenderType {
    CLIENT, // Mensagem enviada pelo usuário (você)
    AGENT,  // Mensagem enviada pelo sistema (WTC, CRM, Marketing)
}

enum class MessageType {
    TEXT,
    CAMPAIGN, // Mensagem rica (promoção, banner, evento) - Agora com interação
    SYSTEM,   // Mensagens automáticas do sistema
    USER_MESSAGE // Mensagens de texto simples do usuário
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
            onSurface = Color(0xFF1E293B),
            // Cor secundária usada para balões de chat do usuário
            secondaryContainer = Color(0xFFD9E9FF), // Azul claro para balão do cliente
            onSecondaryContainer = Color(0xFF001F3F),
            error = Color(0xFFD9183B) // Vermelho forte para erro/alerta
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WTCClientApp() {
    // Simulação de dados de perfil do usuário (Personalização/Integrações)
    data class UserProfile(val id: String, val tier: String, val segment: String)
    val userProfile = remember { UserProfile("CLIENTE_42", "Platinum", "Tech Enthusiast") }

    val currentUserId = userProfile.id
    val agentId = "CRM WTC"

    val initialMessages = remember {
        mutableStateListOf(
            Message(
                id = "3",
                sender = agentId,
                content = "Seu boleto de Outubro já está disponível. Acesse o link para pagamento imediato: [Deeplink Boleto]",
                type = MessageType.TEXT,
                actions = listOf(
                    Action("[Deeplink Boleto]", ActionType.DEEPLINK_TEXT, "wtcapp://fatura/outubro")
                ),
                isRead = false
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
                isRead = false, // Mensagem não lida
                likeCount = 15 // Inicialização com likes
            ),
            Message(
                id = "1",
                sender = agentId,
                // Mensagem personalizada baseada no perfil
                content = "Olá, [Nome do Cliente]! Seja bem-vindo à nossa nova experiência de comunicação. Como cliente **${userProfile.tier}**, você receberá ofertas exclusivas. Recebemos seu pedido #4590. Use o comando /suporte para falar com um agente.",
                type = MessageType.SYSTEM
            )
        )
    }

    // State para simular a nova mensagem (Push Notification)
    var showPopup by remember { mutableStateOf(false) }

    // Função que será passada para o InputBar para enviar mensagens
    val onMessageSent: (String) -> Unit = { text ->
        if (text.isNotBlank()) {
            val newMessage = Message(
                id = java.util.UUID.randomUUID().toString(),
                sender = currentUserId,
                content = text.trim(),
                type = MessageType.USER_MESSAGE
            )
            initialMessages.add(0, newMessage)
            FirebaseMock.sendMessageToBackend(newMessage)
        }
    }

    // NOVA FUNÇÃO: Manipula o evento de "Curtir" em uma mensagem
    val onMessageLiked: (String) -> Unit = { messageId ->
        val index = initialMessages.indexOfFirst { it.id == messageId }
        if (index != -1) {
            val oldMessage = initialMessages[index]
            val newIsLiked = !oldMessage.isLiked
            val newLikeCount = if (newIsLiked) oldMessage.likeCount + 1 else oldMessage.likeCount - 1

            // Cria uma nova mensagem com o estado atualizado (imutabilidade)
            val updatedMessage = oldMessage.copy(
                isLiked = newIsLiked,
                likeCount = newLikeCount
            )

            // Substitui a mensagem antiga pela nova na lista mutável
            initialMessages[index] = updatedMessage

            // Simula log de interação para o backend
            val interaction = if(newIsLiked) "CURTIDA" else "DESCURTIDA"
            FirebaseMock.logInteraction(messageId, interaction)
        }
    }


    // Mock de escuta de mensagens em tempo real e simulação
    LaunchedEffect(Unit) {
        FirebaseMock.setupRealtimeListener { newMessage ->
            initialMessages.add(0, newMessage)
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
            isRead = false,
            likeCount = 3
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
        bottomBar = {
            InputBar(onMessageSent = onMessageSent)
        },
        content = { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                // Passa a função de like para a lista de mensagens
                MessageList(initialMessages, currentUserId, onMessageLiked)

                // Popup de notificação in-app (push simulado)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    AnimatedVisibility(
                        visible = showPopup,
                        enter = fadeIn(animationSpec = tween(500)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        // Verifica se initialMessages não está vazia antes de acessar o primeiro elemento
                        if (initialMessages.isNotEmpty()) {
                            InAppNotification(message = initialMessages.first())
                        }
                    }
                }

                // Indicador de mensagens não lidas
                val unreadCount = initialMessages.count { !it.isRead && it.sender != currentUserId }
                if (unreadCount > 0) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 60.dp) // Ajustado para não colidir com o InputBar
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
fun MessageList(
    messages: MutableList<Message>,
    currentUserId: String,
    onMessageLiked: (String) -> Unit // NOVO PARÂMETRO
) {
    val listState = rememberLazyListState()

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
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
        reverseLayout = true,
        state = listState
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
                                Log.d("WTCApp", "Tarefa rápida criada para a mensagem (Long Press): ${message.id}")
                            }
                        )
                    },
                horizontalArrangement = if (message.sender == currentUserId) Arrangement.End else Arrangement.Start
            ) {
                // Conteúdo da mensagem
                // Passa a função de like para o card
                RichMessageCard(message, currentUserId, onLikeClicked = onMessageLiked)

                // Área para mostrar ação de importante
                if (message.isImportant) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Importante",
                        tint = Color(0xFFF5C71A), // Amarelo
                        modifier = Modifier.padding(start = 8.dp).size(24.dp).align(Alignment.CenterVertically)
                    )
                }
            }
            // Separador (somente para mensagens que não são de chat em balão)
            if (message.type != MessageType.USER_MESSAGE && message.type != MessageType.TEXT && index < messages.size - 1) {
                Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

// --- Novo: Barra de Entrada de Texto ---
@Composable
fun InputBar(onMessageSent: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Surface(
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Campo de entrada de texto
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Digite sua mensagem...") },
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    // Ícone de anexar ou emoji (opcional)
                    Icon(Icons.Filled.AttachFile, contentDescription = "Anexar", tint = Color.Gray)
                },
                singleLine = true
            )

            // Botão de Enviar
            Button(
                onClick = {
                    onMessageSent(text)
                    text = "" // Limpa o campo após o envio
                },
                enabled = text.isNotBlank(), // Só habilita se houver texto
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Enviar", modifier = Modifier.size(24.dp))
            }
        }
    }
}


// --- Renderização de Mensagem Rica (Individual) ---

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RichMessageCard(
    message: Message,
    currentUserId: String,
    onLikeClicked: (String) -> Unit // NOVO PARÂMETRO PARA INTERAÇÃO
) {
    val context = LocalContext.current

    val senderType = if (message.sender == currentUserId) SenderType.CLIENT else SenderType.AGENT
    val isUserMessage = senderType == SenderType.CLIENT && message.type == MessageType.USER_MESSAGE

    val backgroundColor = when {
        isUserMessage -> MaterialTheme.colorScheme.secondaryContainer
        message.type == MessageType.CAMPAIGN -> Color(0xFFE5F6FF)
        message.type == MessageType.SYSTEM -> Color(0xFFFFFBE5)
        else -> Color.White
    }

    val contentColor = if (isUserMessage) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface

    val cardShape = when (senderType) {
        SenderType.CLIENT -> RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
        SenderType.AGENT -> RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)
    }

    Card(
        modifier = Modifier
            .widthIn(max = 300.dp)
            .then(if (isUserMessage) Modifier else Modifier.fillMaxWidth()),
        shape = if (isUserMessage) cardShape else MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Cabeçalho (Remetente e Data)
            if (!isUserMessage) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = message.sender,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!message.isRead) {
                            Icon(Icons.Filled.FiberManualRecord, contentDescription = "Não Lido", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(10.dp))
                            Spacer(Modifier.width(4.dp))
                        }
                        Text(
                            text = java.text.SimpleDateFormat("HH:mm").format(message.timestamp),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                // Para mensagens do usuário, apenas a hora
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = java.text.SimpleDateFormat("HH:mm").format(message.timestamp),
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }


            // Conteúdo da Mensagem (Texto com tratamento de Deeplinks)
            Text(
                text = message.content,
                fontSize = 16.sp,
                color = contentColor,
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
                        }
                        .padding(vertical = 4.dp)
                )
            }

            // Renderiza Botões de Ação
            if (message.actions.any { it.type == ActionType.BUTTON_LINK || it.type == ActionType.BUTTON_COMMAND }) {
                Spacer(modifier = Modifier.height(12.dp))
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

            // NOVO: Interação do Usuário (Curtir/Comentar)
            if (message.type == MessageType.CAMPAIGN || message.type == MessageType.TEXT) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botão "Curtir" (Simulação de Interação)
                    Row(
                        modifier = Modifier.clickable { onLikeClicked(message.id) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (message.isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Curtir promoção",
                            tint = if (message.isLiked) MaterialTheme.colorScheme.error else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (message.likeCount > 0) message.likeCount.toString() else "Curtir",
                            fontSize = 14.sp,
                            color = if (message.isLiked) MaterialTheme.colorScheme.error else Color.Gray,
                            fontWeight = if (message.isLiked) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Comentários (Simulado)
                    Row(
                        modifier = Modifier.clickable { Log.d("WTCApp", "Abrir tela de comentários para ${message.id}") },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Comment,
                            contentDescription = "Comentários",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Comentar",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
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