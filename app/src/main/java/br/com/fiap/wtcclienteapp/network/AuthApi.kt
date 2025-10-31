package br.com.fiap.wtcclienteapp.network

import br.com.fiap.wtcclienteapp.network.model.LoginRequest
import br.com.fiap.wtcclienteapp.network.model.LoginResponse
import br.com.fiap.wtcclienteapp.network.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>
}
