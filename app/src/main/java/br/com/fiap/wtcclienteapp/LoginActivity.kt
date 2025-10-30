package br.com.fiap.wtcclienteapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnTabColaborador = findViewById<Button>(R.id.btnTabColaborador)
        val btnTabCliente = findViewById<Button>(R.id.btnTabCliente)

        val formColaborador = findViewById<View>(R.id.formColaborador)
        val formCliente = findViewById<View>(R.id.formCliente)

        fun showColaborador() {
            formColaborador.visibility = View.VISIBLE
            formCliente.visibility = View.GONE
        }

        fun showCliente() {
            formColaborador.visibility = View.GONE
            formCliente.visibility = View.VISIBLE
        }

        btnTabColaborador.setOnClickListener { showColaborador() }
        btnTabCliente.setOnClickListener { showCliente() }

        // Default: mostrar cliente
        showCliente()

        // Ações dos botões de entrar
        findViewById<Button>(R.id.btnEntrarColaborador).setOnClickListener {
            val email = findViewById<EditText>(R.id.inputEmailColab).text.toString()
            val senha = findViewById<EditText>(R.id.inputSenhaColab).text.toString()
            // TODO: validar/autenticar
            startActivity(Intent(this, OperadorActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.btnEntrarCliente).setOnClickListener {
            val cpf = findViewById<EditText>(R.id.inputCpfCliente).text.toString()
            val senha = findViewById<EditText>(R.id.inputSenhaCliente).text.toString()
            // TODO: validar/autenticar
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}


