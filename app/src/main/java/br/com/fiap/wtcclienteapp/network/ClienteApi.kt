package br.com.fiap.wtcclienteapp.network

import br.com.fiap.wtcclienteapp.network.model.ClienteApiResponse
import br.com.fiap.wtcclienteapp.network.model.ClienteCreateRequest
import br.com.fiap.wtcclienteapp.network.model.ClienteUpdateRequest
import retrofit2.Response
import retrofit2.http.*

interface ClienteApi {
    @GET("api/clientes")
    suspend fun listarClientes(): List<ClienteApiResponse>
    
    @GET("api/clientes/{id}")
    suspend fun buscarClientePorId(@Path("id") id: Long): ClienteApiResponse
    
    @GET("api/clientes/tag/{tag}")
    suspend fun buscarClientesPorTag(@Path("tag") tag: String): List<ClienteApiResponse>
    
    @POST("api/clientes/{usuarioId}")
    suspend fun criarCliente(
        @Path("usuarioId") usuarioId: Long,
        @Body request: ClienteCreateRequest
    ): Response<ClienteApiResponse>
    
    @PUT("api/clientes/{id}")
    suspend fun atualizarCliente(
        @Path("id") id: Long,
        @Body request: ClienteUpdateRequest
    ): Response<ClienteApiResponse>
}
