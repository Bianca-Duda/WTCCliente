package br.com.fiap.wtcclienteapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcclienteapp.model.Cliente
import kotlinx.coroutines.launch

class ClienteViewModel : ViewModel() {
    private val repository = ClienteRepository()
    private val _clientes = MutableLiveData<List<Cliente>>()
    val clientes: LiveData<List<Cliente>> = _clientes
    
    private val _clienteSelecionado = MutableLiveData<Cliente?>()
    val clienteSelecionado: LiveData<Cliente?> = _clienteSelecionado
    
    private val _erro = MutableLiveData<String?>()
    val erro: LiveData<String?> = _erro
    private val _sucesso = MutableLiveData<String?>()
    val sucesso: LiveData<String?> = _sucesso

    fun filtrarClientes(filtros: Filtros) {
        viewModelScope.launch {
            try {
                // Se há ID, buscar apenas esse cliente
                val resultado = if (filtros.id != null) {
                    val cliente = repository.buscarClientePorId(filtros.id)
                    cliente?.let { listOf(it) } ?: emptyList()
                } else {
                    repository.buscarClientes(filtros)
                }
                _clientes.postValue(resultado)
                _erro.postValue(null)
            } catch (e: Exception) {
                _clientes.postValue(emptyList())
                _erro.postValue("Erro ao buscar clientes: ${e.message}")
            }
        }
    }
    
    fun buscarClientePorId(id: Long) {
        viewModelScope.launch {
            try {
                val cliente = repository.buscarClientePorId(id)
                _clienteSelecionado.postValue(cliente)
                if (cliente == null) {
                    _erro.postValue("Cliente não encontrado")
                } else {
                    _erro.postValue(null)
                }
            } catch (e: Exception) {
                _clienteSelecionado.postValue(null)
                _erro.postValue("Erro ao buscar cliente: ${e.message}")
            }
        }
    }
    
    fun buscarClientesPorTag(tag: String) {
        viewModelScope.launch {
            try {
                val clientes = repository.buscarClientesPorTag(tag)
                _clientes.postValue(clientes)
                _erro.postValue(null)
            } catch (e: Exception) {
                _clientes.postValue(emptyList())
                _erro.postValue("Erro ao buscar por tag: ${e.message}")
            }
        }
    }
    
    fun criarCliente(usuarioId: Long, cpf: String, status: String, scoreCrm: Int, observacoes: String?, tags: List<String>) {
        viewModelScope.launch {
            try {
                val cliente = repository.criarCliente(usuarioId, cpf, status, scoreCrm, observacoes, tags)
                if (cliente != null) {
                    _sucesso.postValue("Cliente criado com sucesso!")
                    // Recarregar lista
                    filtrarClientes(Filtros())
                } else {
                    _erro.postValue("Erro ao criar cliente")
                }
            } catch (e: Exception) {
                _erro.postValue("Erro ao criar cliente: ${e.message}")
            }
        }
    }
    
    fun atualizarCliente(id: Long, cpf: String? = null, status: String? = null, scoreCrm: Int? = null, observacoes: String? = null, tags: List<String>? = null) {
        viewModelScope.launch {
            try {
                val cliente = repository.atualizarCliente(id, cpf, status, scoreCrm, observacoes, tags)
                if (cliente != null) {
                    _sucesso.postValue("Cliente atualizado com sucesso!")
                    // Recarregar lista
                    filtrarClientes(Filtros())
                } else {
                    _erro.postValue("Erro ao atualizar cliente")
                }
            } catch (e: Exception) {
                _erro.postValue("Erro ao atualizar cliente: ${e.message}")
            }
        }
    }
    
    fun limparMensagens() {
        _erro.postValue(null)
        _sucesso.postValue(null)
    }
}


