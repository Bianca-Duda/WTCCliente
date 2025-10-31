package br.com.fiap.wtcclienteapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.fiap.wtcclienteapp.network.AuthManager

class OperadorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operador)

        // Inicializar AuthManager para garantir que o token está disponível
        AuthManager.initialize(this)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerOperador, ClienteListFragment())
                .commit()
        }
    }
}


