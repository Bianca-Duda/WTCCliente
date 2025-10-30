package br.com.fiap.wtcclienteapp.model

data class Cliente(
    val id: Long,
    val nome: String,
    val cpf: String,
    val status: Status,
    val scoreCrm: Int,
    val observacoes: String,
    val tags: List<String>
) {
    val score: Score
        get() = Score(scoreCrm)
}


