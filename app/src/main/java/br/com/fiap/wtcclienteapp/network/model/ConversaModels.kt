package br.com.fiap.wtcclienteapp.network.model

data class ConversaCreateRequest(
    val titulo: String,
    val participantesIds: List<Long>,
    val ehGrupo: Boolean
)

data class ConversaResponse(
    val id: Long?,
    val titulo: String?,
    val ehGrupo: Boolean?,
    val participantes: List<ParticipanteResponse>?,
    val dataCriacao: String?,
    val ultimaMensagem: String?,
    val arquivada: Boolean?
)

data class ParticipanteResponse(
    val id: Long,
    val email: String,
    val senha: String?,
    val nome: String,
    val telefone: String?,
    val tipo: String,
    val ativo: Boolean?,
    val dataCadastro: String?,
    val ultimoAcesso: String?,
    val credentialsNonExpired: Boolean?,
    val accountNonExpired: Boolean?,
    val accountNonLocked: Boolean?,
    val username: String?,
    val password: String?,
    val authorities: List<AuthorityResponse>?,
    val enabled: Boolean?
)
