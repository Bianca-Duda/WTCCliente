package br.com.fiap.wtcclienteapp

import br.com.fiap.wtcclienteapp.model.Cliente
import br.com.fiap.wtcclienteapp.network.RetrofitInstance
import br.com.fiap.wtcclienteapp.network.model.ClienteCreateRequest
import br.com.fiap.wtcclienteapp.network.model.ClienteUpdateRequest

class ClienteRepository {
    private val api = RetrofitInstance.clienteApi

    suspend fun buscarClientes(filtros: Filtros): List<Cliente> {
        return try {
            // Se há filtro por tag, buscar por tag primeiro
            val clientesApi = if (!filtros.tag.isNullOrEmpty()) {
                api.buscarClientesPorTag(filtros.tag)
            } else {
                api.listarClientes()
            }
            
            // Converter para o modelo Cliente
            val clientes = clientesApi.map { Cliente.fromApiResponse(it) }
            
            // Aplicar outros filtros
            clientes.filter { c ->
                (filtros.score == null || c.scoreCrm >= filtros.score) &&
                (filtros.status.isNullOrEmpty() || c.status.descricao.contains(filtros.status, true)) &&
                (filtros.cpf.isNullOrEmpty() || c.cpf.contains(filtros.cpf, true))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun buscarClientePorId(id: Long): Cliente? {
        return try {
            val clienteApi = api.buscarClientePorId(id)
            Cliente.fromApiResponse(clienteApi)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun buscarClientesPorTag(tag: String): List<Cliente> {
        return try {
            val clientesApi = api.buscarClientesPorTag(tag)
            clientesApi.map { Cliente.fromApiResponse(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun criarCliente(usuarioId: Long, cpf: String, status: String, scoreCrm: Int, observacoes: String?, tags: List<String>): Cliente? {
        return try {
            val request = ClienteCreateRequest(
                cpf = cpf,
                status = status,
                scoreCrm = scoreCrm,
                observacoes = observacoes,
                tags = tags
            )
            val response = api.criarCliente(usuarioId, request)
            if (response.isSuccessful && response.body() != null) {
                Cliente.fromApiResponse(response.body()!!)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun atualizarCliente(id: Long, cpf: String? = null, status: String? = null, scoreCrm: Int? = null, observacoes: String? = null, tags: List<String>? = null): Cliente? {
        return try {
            val request = ClienteUpdateRequest(
                cpf = cpf,
                status = status,
                scoreCrm = scoreCrm,
                observacoes = observacoes,
                tags = tags
            )
            val response = api.atualizarCliente(id, request)
            if (response.isSuccessful && response.body() != null) {
                Cliente.fromApiResponse(response.body()!!)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


