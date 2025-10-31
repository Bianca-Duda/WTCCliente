package br.com.fiap.wtcclienteapp

import br.com.fiap.wtcclienteapp.network.RetrofitInstance
import br.com.fiap.wtcclienteapp.network.model.MensagemCreateRequest
import br.com.fiap.wtcclienteapp.network.model.MensagemResponse

class MensagemRepository {
    private val api = RetrofitInstance.mensagemApi

    suspend fun enviarMensagem(conversaId: Long, conteudo: String): MensagemResponse? {
        return try {
            val request = MensagemCreateRequest(conversaId = conversaId, conteudo = conteudo)
            val response = api.enviarMensagem(request)
            if (response.isSuccessful && response.body() != null) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun listarMensagens(conversaId: Long, page: Int = 0, size: Int = 20): List<MensagemResponse> {
        return try {
            api.listarMensagens(conversaId, page, size)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun marcarComoLida(mensagemId: Long): Boolean {
        return try {
            val response = api.marcarComoLida(mensagemId)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    suspend fun contarMensagensNaoLidas(): Int {
        return try {
            api.contarMensagensNaoLidas()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
}
