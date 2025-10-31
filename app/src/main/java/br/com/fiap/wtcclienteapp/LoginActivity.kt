package br.com.fiap.wtcclienteapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.fiap.wtcclienteapp.network.AuthManager
import br.com.fiap.wtcclienteapp.network.RetrofitInstance
import br.com.fiap.wtcclienteapp.network.model.LoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar AuthManager
        AuthManager.initialize(this)

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
            val email = findViewById<EditText>(R.id.inputEmailColab).text.toString().trim()
            val senha = findViewById<EditText>(R.id.inputSenhaColab).text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performLogin(email, senha, isOperador = true)
        }

        findViewById<Button>(R.id.btnEntrarCliente).setOnClickListener {
            val email = findViewById<EditText>(R.id.inputEmailCliente).text.toString().trim()
            val senha = findViewById<EditText>(R.id.inputSenhaCliente).text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performLogin(email, senha, isOperador = false)
        }
    }

    private fun performLogin(email: String, senha: String, isOperador: Boolean) {
        val loginButton = if (isOperador) {
            findViewById<Button>(R.id.btnEntrarColaborador)
        } else {
            findViewById<Button>(R.id.btnEntrarCliente)
        }

        loginButton.isEnabled = false
        loginButton.text = "Entrando..."

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val loginRequest = LoginRequest(email = email, senha = senha)
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.authApi.login(loginRequest)
                }

                // Salvar token e informações do usuário
                AuthManager.saveToken(response.token)
                AuthManager.saveUserInfo(
                    userId = response.usuarioId,
                    userName = response.nome,
                    userType = response.tipo,
                    userCpf = null // CPF não vem na resposta do login
                )

                // Navegar para a tela apropriada
                if (isOperador) {
                    // Verificar se o tipo de usuário é OPERADOR
                    val userType = response.tipo?.uppercase()
                    if (userType == "OPERADOR") {
                        startActivity(Intent(this@LoginActivity, OperadorActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Credenciais inválidas para operador",
                            Toast.LENGTH_SHORT
                        ).show()
                        loginButton.isEnabled = true
                        loginButton.text = "Entrar como Colaborador"
                    }
                } else {
                    // Para cliente, usar informações do usuário retornado pela API
                    val clientId = response.usuarioId?.toString() ?: email
                    val clientName = response.nome ?: email
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                        putExtra("client_id", clientId)
                        putExtra("client_name", clientName)
                    })
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@LoginActivity,
                    "Erro ao fazer login: ${e.message ?: "Verifique suas credenciais"}",
                    Toast.LENGTH_LONG
                ).show()
                loginButton.isEnabled = true
                loginButton.text = if (isOperador) "Entrar como Colaborador" else "Entrar como Cliente"
            }
        }
    }
}


