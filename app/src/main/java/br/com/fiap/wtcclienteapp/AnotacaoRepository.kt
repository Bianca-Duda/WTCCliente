package br.com.fiap.wtcclienteapp

import br.com.fiap.wtcclienteapp.network.RetrofitInstance
import br.com.fiap.wtcclienteapp.network.model.AnotacaoCreateRequest
import br.com.fiap.wtcclienteapp.network.model.AnotacaoResponse

class AnotacaoRepository {
    private val api = RetrofitInstance.anotacaoApi

    suspend fun criarAnotacao(clienteId: Long, texto: String): AnotacaoResponse? {
        return try {
            val request = AnotacaoCreateRequest(clienteId = clienteId, texto = texto)
            val response = api.criarAnotacao(request)
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

    suspend fun listarAnotacoesPorCliente(clienteId: Long): List<AnotacaoResponse> {
        return try {
            api.listarAnotacoesPorCliente(clienteId)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
