package br.com.fiap.wtcclienteapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class OperadorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operador)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerOperador, ClienteListFragment())
                .commit()
        }
    }
}


