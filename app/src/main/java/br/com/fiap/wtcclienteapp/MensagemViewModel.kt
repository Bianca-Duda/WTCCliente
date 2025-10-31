package br.com.fiap.wtcclienteapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcclienteapp.network.model.MensagemResponse
import kotlinx.coroutines.launch

class MensagemViewModel : ViewModel() {
    private val repository = MensagemRepository()
    private var useMock = true // Flag para usar mock
    
    private val _erro = MutableLiveData<String?>()
    val erro: LiveData<String?> = _erro
    private val _sucesso = MutableLiveData<String?>()
    val sucesso: LiveData<String?> = _sucesso
    private val _mensagens = MutableLiveData<List<MensagemResponse>>()
    val mensagens: LiveData<List<MensagemResponse>> = _mensagens
    private val _mensagensNaoLidas = MutableLiveData<Int>()
    val mensagensNaoLidas: LiveData<Int> = _mensagensNaoLidas
    
    private fun getRemetenteNome(): String {
        val authManager = br.com.fiap.wtcclienteapp.network.AuthManager
        return authManager.getUserName() ?: "Operador"
    }

    fun enviarMensagem(conversaId: Long, conteudo: String) {
        viewModelScope.launch {
            try {
                if (useMock) {
                    // Versão mockada - criar mensagem localmente
                    val mensagem = MensagemResponse(
                        id = System.currentTimeMillis(),
                        conversaId = conversaId,
                        conteudo = conteudo,
                        remetenteId = br.com.fiap.wtcclienteapp.network.AuthManager.getUserId(),
                        remetenteNome = getRemetenteNome(),
                        dataEnvio = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()),
                        lida = false,
                        importante = false
                    )
                    
                    // Adicionar ao mock storage
                    MensagemMockStorage.adicionarMensagem(conversaId, mensagem)
                    
                    // Tentar enviar via API também (em background, sem bloquear)
                    try {
                        repository.enviarMensagem(conversaId, conteudo)
                    } catch (e: Exception) {
                        // Se API falhar, continua com mock
                    }
                    
                    _sucesso.postValue("Mensagem enviada")
                    // Recarregar mensagens
                    listarMensagens(conversaId)
                } else {
                    // Versão real da API
                    val mensagem = repository.enviarMensagem(conversaId, conteudo)
                    if (mensagem != null) {
                        _sucesso.postValue("Mensagem enviada")
                        listarMensagens(conversaId)
                    } else {
                        _erro.postValue("Erro ao enviar mensagem")
                    }
                }
            } catch (e: Exception) {
                // Em caso de erro, tentar mock
                if (!useMock) {
                    val mensagem = MensagemResponse(
                        id = System.currentTimeMillis(),
                        conversaId = conversaId,
                        conteudo = conteudo,
                        remetenteId = br.com.fiap.wtcclienteapp.network.AuthManager.getUserId(),
                        remetenteNome = getRemetenteNome(),
                        dataEnvio = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()),
                        lida = false,
                        importante = false
                    )
                    MensagemMockStorage.adicionarMensagem(conversaId, mensagem)
                    _sucesso.postValue("Mensagem enviada (modo offline)")
                    listarMensagens(conversaId)
                } else {
                    _erro.postValue("Erro ao enviar mensagem: ${e.message}")
                }
            }
        }
    }
    
    fun listarMensagens(conversaId: Long, page: Int = 0, size: Int = 20) {
        viewModelScope.launch {
            try {
                if (useMock) {
                    // Buscar do mock storage
                    val listaMock = MensagemMockStorage.obterMensagens(conversaId)
                    
                    // Tentar buscar da API também e mesclar
                    try {
                        val listaApi = repository.listarMensagens(conversaId, page, size)
                        // Mesclar: API primeiro, depois mock
                        val todas = (listaApi + listaMock).distinctBy { it.id }
                        _mensagens.postValue(todas)
                    } catch (e: Exception) {
                        // Se API falhar, usar apenas mock
                        _mensagens.postValue(listaMock)
                    }
                    _erro.postValue(null)
                } else {
                    // Versão real da API
                    val lista = repository.listarMensagens(conversaId, page, size)
                    _mensagens.postValue(lista)
                    _erro.postValue(null)
                }
            } catch (e: Exception) {
                // Em caso de erro, usar mock
                val listaMock = MensagemMockStorage.obterMensagens(conversaId)
                _mensagens.postValue(listaMock)
                _erro.postValue(null) // Não mostrar erro se temos mock
            }
        }
    }
    
    fun marcarComoLida(mensagemId: Long) {
        viewModelScope.launch {
            repository.marcarComoLida(mensagemId)
            // Atualizar contador
            atualizarMensagensNaoLidas()
        }
    }
    
    fun atualizarMensagensNaoLidas() {
        viewModelScope.launch {
            try {
                if (useMock) {
                    // Contar mensagens não lidas do mock
                    val totalNaoLidas = MensagemMockStorage.obterTodasMensagensNaoLidas()
                    _mensagensNaoLidas.postValue(totalNaoLidas)
                    
                    // Tentar buscar da API também
                    try {
                        val count = repository.contarMensagensNaoLidas()
                        _mensagensNaoLidas.postValue(count + totalNaoLidas)
                    } catch (e: Exception) {
                        // Se API falhar, usar apenas mock
                    }
                } else {
                    val count = repository.contarMensagensNaoLidas()
                    _mensagensNaoLidas.postValue(count)
                }
            } catch (e: Exception) {
                // Em caso de erro, contar do mock (simplificado)
                _mensagensNaoLidas.postValue(0)
            }
        }
    }
    
    fun limparMensagens() {
        _erro.postValue(null)
        _sucesso.postValue(null)
    }
}
