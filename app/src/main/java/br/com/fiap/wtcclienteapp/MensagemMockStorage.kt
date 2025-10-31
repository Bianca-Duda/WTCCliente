package br.com.fiap.wtcclienteapp

import br.com.fiap.wtcclienteapp.network.model.MensagemResponse

object MensagemMockStorage {
    private val mensagensPorConversa = mutableMapOf<Long, MutableList<MensagemResponse>>()
    
    fun adicionarMensagem(conversaId: Long, mensagem: MensagemResponse) {
        val lista = mensagensPorConversa.getOrPut(conversaId) { mutableListOf() }
        lista.add(mensagem)
    }
    
    fun obterMensagens(conversaId: Long): List<MensagemResponse> {
        return mensagensPorConversa[conversaId] ?: emptyList()
    }
    
    fun obterTodasMensagensNaoLidas(): Int {
        return mensagensPorConversa.values
            .flatten()
            .count { !(it.lida ?: false) }
    }
    
    fun limpar(conversaId: Long) {
        mensagensPorConversa.remove(conversaId)
    }
}
