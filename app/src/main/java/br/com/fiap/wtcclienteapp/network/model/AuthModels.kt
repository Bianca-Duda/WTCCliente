package br.com.fiap.wtcclienteapp.network.model

data class LoginRequest(
    val email: String,
    val senha: String
)

data class LoginResponse(
    val token: String,
    val usuarioId: Long?,
    val nome: String?,
    val email: String?,
    val tipo: String?
)

data class RegisterRequest(
    val nome: String,
    val email: String,
    val senha: String,
    val telefone: String,
    val tipo: String
)
