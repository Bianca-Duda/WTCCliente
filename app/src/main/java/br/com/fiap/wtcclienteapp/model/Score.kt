package br.com.fiap.wtcclienteapp.model

data class Score(
    val valor: Int
) {
    fun categoria(): CategoriaScore {
        return when {
            valor >= 80 -> CategoriaScore.EXCELENTE
            valor >= 60 -> CategoriaScore.BOM
            valor >= 40 -> CategoriaScore.REGULAR
            else -> CategoriaScore.BAIXO
        }
    }

    enum class CategoriaScore {
        EXCELENTE, BOM, REGULAR, BAIXO
    }
}


