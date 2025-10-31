package br.com.fiap.wtcclienteapp.model

import java.io.Serializable

data class Cliente(
    val id: Long,
    val nome: String,
    val cpf: String,
    val status: Status,
    val scoreCrm: Int,
    val observacoes: String,
    val tags: List<String>,
    val email: String? = null,
    val telefone: String? = null,
    val valorTotalCompras: Double = 0.0
) : Serializable {
    val score: Score
        get() = Score(scoreCrm)
    
    companion object {
        fun fromApiResponse(apiResponse: br.com.fiap.wtcclienteapp.network.model.ClienteApiResponse): Cliente {
            // Converter status da API para enum Status
            val statusEnum = when (apiResponse.status.uppercase()) {
                "ATIVO" -> Status.ATIVO
                "INATIVO" -> Status.INATIVO
                "VIP" -> Status.VIP
                else -> Status.ATIVO
            }
            
            return Cliente(
                id = apiResponse.id,
                nome = apiResponse.usuario.nome,
                cpf = apiResponse.cpf,
                status = statusEnum,
                scoreCrm = apiResponse.scoreCrm,
                observacoes = apiResponse.observacoes ?: "",
                tags = apiResponse.tags,
                email = apiResponse.usuario.email,
                telefone = apiResponse.usuario.telefone,
                valorTotalCompras = apiResponse.valorTotalCompras
            )
        }
    }
}


