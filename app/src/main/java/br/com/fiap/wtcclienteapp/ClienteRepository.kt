package br.com.fiap.wtcclienteapp

import br.com.fiap.wtcclienteapp.model.Cliente
import br.com.fiap.wtcclienteapp.model.Status
import br.com.fiap.wtcclienteapp.network.DummyJsonApi
import br.com.fiap.wtcclienteapp.network.RetrofitInstance

class ClienteRepository {
    private val api: DummyJsonApi = RetrofitInstance.dummyApi

    suspend fun buscarClientes(filtros: Filtros): List<Cliente> {
        val base = if (!filtros.status.isNullOrEmpty()) {
            api.searchUsers(filtros.status).users
        } else if (!filtros.tag.isNullOrEmpty()) {
            api.searchUsers(filtros.tag).users
        } else {
            api.listUsers().users
        }
        val mapped = base.map {
            Cliente(
                id = it.id.toLong(),
                nome = "${it.firstName} ${it.lastName}",
                cpf = it.ssn ?: it.id.toString(),
                status = Status.ATIVO,
                scoreCrm = (it.age ?: 50).coerceIn(0, 100),
                observacoes = "",
                tags = listOf(it.gender ?: "")
            )
        }
        return mapped.filter { c ->
            (filtros.score == null || c.scoreCrm >= filtros.score) &&
            (filtros.status.isNullOrEmpty() || c.status.descricao.contains(filtros.status, true)) &&
            (filtros.tag.isNullOrEmpty() || c.tags.any { it.contains(filtros.tag, true) })
        }
    }
}


