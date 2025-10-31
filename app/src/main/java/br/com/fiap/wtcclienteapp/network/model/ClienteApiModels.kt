package br.com.fiap.wtcclienteapp.network.model

data class ClienteApiResponse(
    val id: Long,
    val usuario: UsuarioApiResponse,
    val cpf: String,
    val dataNascimento: String?,
    val status: String,
    val scoreCrm: Int,
    val observacoes: String?,
    val tags: List<String>,
    val ultimaCompra: String?,
    val valorTotalCompras: Double
)

data class UsuarioApiResponse(
    val id: Long,
    val email: String,
    val senha: String?,
    val nome: String,
    val telefone: String,
    val tipo: String,
    val ativo: Boolean,
    val dataCadastro: String,
    val ultimoAcesso: String?,
    val credentialsNonExpired: Boolean,
    val accountNonExpired: Boolean,
    val accountNonLocked: Boolean,
    val username: String,
    val password: String?,
    val authorities: List<AuthorityResponse>,
    val enabled: Boolean
)

data class AuthorityResponse(
    val authority: String
)
