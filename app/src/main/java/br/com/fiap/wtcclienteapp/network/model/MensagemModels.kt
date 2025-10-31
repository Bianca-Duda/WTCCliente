package br.com.fiap.wtcclienteapp.network.model

data class MensagemCreateRequest(
    val conversaId: Long,
    val conteudo: String
)

data class MensagemResponse(
    val id: Long?,
    val conversaId: Long?,
    val conteudo: String?,
    val remetenteId: Long?,
    val remetenteNome: String?,
    val dataEnvio: String?,
    val lida: Boolean?,
    val importante: Boolean?
)
