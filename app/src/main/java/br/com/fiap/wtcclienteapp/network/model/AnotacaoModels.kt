package br.com.fiap.wtcclienteapp.network.model

data class AnotacaoCreateRequest(
    val clienteId: Long,
    val texto: String
)

data class AnotacaoResponse(
    val id: Long?,
    val clienteId: Long?,
    val texto: String?,
    val dataCriacao: String?,
    val operadorId: Long?,
    val operadorNome: String?
)
