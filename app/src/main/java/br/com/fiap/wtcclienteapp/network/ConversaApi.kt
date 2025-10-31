package br.com.fiap.wtcclienteapp.network

import br.com.fiap.wtcclienteapp.network.model.ConversaCreateRequest
import br.com.fiap.wtcclienteapp.network.model.ConversaResponse
import retrofit2.Response
import retrofit2.http.*

interface ConversaApi {
    @GET("api/conversas")
    suspend fun listarConversas(): List<ConversaResponse>
    
    @POST("api/conversas")
    suspend fun criarConversa(@Body request: ConversaCreateRequest): Response<ConversaResponse>
}
