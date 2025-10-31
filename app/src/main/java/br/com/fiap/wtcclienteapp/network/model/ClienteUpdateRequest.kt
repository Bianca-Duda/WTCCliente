package br.com.fiap.wtcclienteapp.network.model

data class ClienteUpdateRequest(
    val cpf: String? = null,
    val status: String? = null,
    val scoreCrm: Int? = null,
    val observacoes: String? = null,
    val tags: List<String>? = null
)
