package br.com.fiap.wtcclienteapp.network

import br.com.fiap.wtcclienteapp.network.model.AnotacaoCreateRequest
import br.com.fiap.wtcclienteapp.network.model.AnotacaoResponse
import retrofit2.Response
import retrofit2.http.*

interface AnotacaoApi {
    @POST("api/anotacoes")
    suspend fun criarAnotacao(@Body request: AnotacaoCreateRequest): Response<AnotacaoResponse>
    
    @GET("api/anotacoes/cliente/{clienteId}")
    suspend fun listarAnotacoesPorCliente(@Path("clienteId") clienteId: Long): List<AnotacaoResponse>
}
