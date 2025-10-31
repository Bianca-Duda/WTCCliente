package br.com.fiap.wtcclienteapp.network

import br.com.fiap.wtcclienteapp.network.model.LoginRequest
import br.com.fiap.wtcclienteapp.network.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
