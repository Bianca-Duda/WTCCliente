package br.com.fiap.wtcclienteapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcclienteapp.network.model.ConversaResponse
import kotlinx.coroutines.launch

class ConversaViewModel : ViewModel() {
    private val repository = ConversaRepository()
    
    private val _erro = MutableLiveData<String?>()
    val erro: LiveData<String?> = _erro
    private val _sucesso = MutableLiveData<String?>()
    val sucesso: LiveData<String?> = _sucesso
    private val _conversas = MutableLiveData<List<ConversaResponse>>()
    val conversas: LiveData<List<ConversaResponse>> = _conversas

    fun criarConversa(titulo: String, participantesIds: List<Long>, ehGrupo: Boolean) {
        viewModelScope.launch {
            try {
                val conversa = repository.criarConversa(titulo, participantesIds, ehGrupo)
                if (conversa != null) {
                    _sucesso.postValue(if (ehGrupo) "Grupo criado com sucesso!" else "Conversa criada com sucesso!")
                    // Recarregar lista de conversas
                    listarConversas()
                } else {
                    _erro.postValue("Erro ao criar conversa/grupo")
                }
            } catch (e: Exception) {
                _erro.postValue("Erro ao criar conversa/grupo: ${e.message}")
            }
        }
    }
    
    fun listarConversas() {
        viewModelScope.launch {
            try {
                val lista = repository.listarConversas()
                _conversas.postValue(lista)
                _erro.postValue(null)
            } catch (e: Exception) {
                _conversas.postValue(emptyList())
                _erro.postValue("Erro ao listar conversas: ${e.message}")
            }
        }
    }
    
    fun limparMensagens() {
        _erro.postValue(null)
        _sucesso.postValue(null)
    }
}
