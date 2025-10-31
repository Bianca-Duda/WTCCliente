package br.com.fiap.wtcclienteapp.network.model

data class ClienteCreateRequest(
    val cpf: String,
    val status: String,
    val scoreCrm: Int,
    val observacoes: String?,
    val tags: List<String>
)
