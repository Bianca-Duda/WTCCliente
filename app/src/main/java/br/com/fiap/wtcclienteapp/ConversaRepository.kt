package br.com.fiap.wtcclienteapp

import br.com.fiap.wtcclienteapp.network.RetrofitInstance
import br.com.fiap.wtcclienteapp.network.model.ConversaCreateRequest
import br.com.fiap.wtcclienteapp.network.model.ConversaResponse

class ConversaRepository {
    private val api = RetrofitInstance.conversaApi

    suspend fun listarConversas(): List<ConversaResponse> {
        return try {
            api.listarConversas()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun criarConversa(titulo: String, participantesIds: List<Long>, ehGrupo: Boolean): ConversaResponse? {
        return try {
            val request = ConversaCreateRequest(
                titulo = titulo,
                participantesIds = participantesIds,
                ehGrupo = ehGrupo
            )
            val response = api.criarConversa(request)
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
}
