package br.com.fiap.wtcclienteapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcclienteapp.network.model.AnotacaoResponse
import kotlinx.coroutines.launch

class AnotacaoViewModel : ViewModel() {
    private val repository = AnotacaoRepository()
    
    private val _erro = MutableLiveData<String?>()
    val erro: LiveData<String?> = _erro
    private val _sucesso = MutableLiveData<String?>()
    val sucesso: LiveData<String?> = _sucesso
    private val _anotacoes = MutableLiveData<List<AnotacaoResponse>>()
    val anotacoes: LiveData<List<AnotacaoResponse>> = _anotacoes

    fun criarAnotacao(clienteId: Long, texto: String) {
        viewModelScope.launch {
            try {
                val anotacao = repository.criarAnotacao(clienteId, texto)
                if (anotacao != null) {
                    _sucesso.postValue("Anotação criada com sucesso!")
                    // Recarregar anotações do cliente
                    listarAnotacoesPorCliente(clienteId)
                } else {
                    _erro.postValue("Erro ao criar anotação")
                }
            } catch (e: Exception) {
                _erro.postValue("Erro ao criar anotação: ${e.message}")
            }
        }
    }
    
    fun listarAnotacoesPorCliente(clienteId: Long) {
        viewModelScope.launch {
            try {
                val lista = repository.listarAnotacoesPorCliente(clienteId)
                _anotacoes.postValue(lista)
                _erro.postValue(null)
            } catch (e: Exception) {
                _anotacoes.postValue(emptyList())
                _erro.postValue("Erro ao listar anotações: ${e.message}")
            }
        }
    }
    
    fun limparMensagens() {
        _erro.postValue(null)
        _sucesso.postValue(null)
    }
}
