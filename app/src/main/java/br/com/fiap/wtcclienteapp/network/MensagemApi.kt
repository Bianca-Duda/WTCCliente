package br.com.fiap.wtcclienteapp.network

import br.com.fiap.wtcclienteapp.network.model.MensagemCreateRequest
import br.com.fiap.wtcclienteapp.network.model.MensagemResponse
import retrofit2.Response
import retrofit2.http.*

interface MensagemApi {
    @POST("api/mensagens")
    suspend fun enviarMensagem(@Body request: MensagemCreateRequest): Response<MensagemResponse>
    
    @GET("api/mensagens/conversa/{conversaId}")
    suspend fun listarMensagens(
        @Path("conversaId") conversaId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): List<MensagemResponse>
    
    @PATCH("api/mensagens/{mensagemId}/lida")
    suspend fun marcarComoLida(@Path("mensagemId") mensagemId: Long): Response<Unit>
    
    @GET("api/mensagens/nao-lidas")
    suspend fun contarMensagensNaoLidas(): Int
}
