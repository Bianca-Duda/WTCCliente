package br.com.fiap.wtcclienteapp

import android.content.Intent
import android.os.Bundle
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

        val inputEmail = findViewById<EditText>(R.id.inputEmail)
        val inputSenha = findViewById<EditText>(R.id.inputSenha)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)

        btnEntrar.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val senha = inputSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performLogin(email, senha)
        }
    }

    private fun performLogin(email: String, senha: String) {
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val originalButtonText = btnEntrar.text.toString()

        btnEntrar.isEnabled = false
        btnEntrar.text = "Entrando..."

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

                // Navegar para a tela apropriada baseado no tipo retornado pela API
                val userType = response.tipo?.uppercase()
                
                when (userType) {
                    "OPERADOR" -> {
                        // Redirecionar para OperadorActivity
                        startActivity(Intent(this@LoginActivity, OperadorActivity::class.java))
                        finish()
                    }
                    "CLIENTE" -> {
                        // Redirecionar para MainActivity (cliente)
                        val clientId = response.usuarioId?.toString() ?: email
                        val clientName = response.nome ?: email
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                            putExtra("client_id", clientId)
                            putExtra("client_name", clientName)
                        })
                        finish()
                    }
                    else -> {
                        // Tipo desconhecido ou não informado
                        Toast.makeText(
                            this@LoginActivity,
                            "Tipo de usuário não reconhecido: ${response.tipo}",
                            Toast.LENGTH_SHORT
                        ).show()
                        btnEntrar.isEnabled = true
                        btnEntrar.text = originalButtonText
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@LoginActivity,
                    "Erro ao fazer login: ${e.message ?: "Verifique suas credenciais"}",
                    Toast.LENGTH_LONG
                ).show()
                btnEntrar.isEnabled = true
                btnEntrar.text = originalButtonText
            }
        }
    }
}


