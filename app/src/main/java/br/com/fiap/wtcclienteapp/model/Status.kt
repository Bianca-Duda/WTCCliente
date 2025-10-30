package br.com.fiap.wtcclienteapp.model

enum class Status(val descricao: String) {
    ATIVO("Ativo"),
    INATIVO("Inativo"),
    VIP("VIP"),
    FIDELIZADO("Fidelizado"),
    EM_ANALISE("Em análise");

    companion object {
        fun from(descricao: String): Status? {
            return values().find { it.descricao.equals(descricao, ignoreCase = true) }
        }
    }
}


