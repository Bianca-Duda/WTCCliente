package br.com.fiap.wtcclienteapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.fiap.wtcclienteapp.network.RetrofitInstance
import br.com.fiap.wtcclienteapp.network.model.RegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val inputNome = findViewById<EditText>(R.id.inputNome)
        val inputEmail = findViewById<EditText>(R.id.inputEmail)
        val inputTelefone = findViewById<EditText>(R.id.inputTelefone)
        val inputSenha = findViewById<EditText>(R.id.inputSenha)
        val inputConfirmarSenha = findViewById<EditText>(R.id.inputConfirmarSenha)
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val btnIrParaLogin = findViewById<Button>(R.id.btnIrParaLogin)

        btnCadastrar.setOnClickListener {
            val nome = inputNome.text.toString().trim()
            val email = inputEmail.text.toString().trim()
            val telefone = inputTelefone.text.toString().trim()
            val senha = inputSenha.text.toString()
            val confirmarSenha = inputConfirmarSenha.text.toString()

            // Validações
            if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha.length < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar se o email é de operador (contém "@crm.com" ou similar) ou cliente
            val tipo = if (email.contains("@crm.com", ignoreCase = true) || 
                          email.contains("@operador", ignoreCase = true) ||
                          email.contains("@colaborador", ignoreCase = true)) {
                "OPERADOR"
            } else {
                "CLIENTE"
            }

            performRegister(nome, email, telefone, senha, tipo)
        }

        btnIrParaLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun performRegister(nome: String, email: String, telefone: String, senha: String, tipo: String) {
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val originalButtonText = btnCadastrar.text.toString()

        btnCadastrar.isEnabled = false
        btnCadastrar.text = "Cadastrando..."

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val registerRequest = RegisterRequest(
                    nome = nome,
                    email = email,
                    senha = senha,
                    telefone = telefone,
                    tipo = tipo
                )
                
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.authApi.register(registerRequest)
                }

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Cadastro realizado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Redirecionar para login
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Dados inválidos. Verifique as informações."
                        409 -> "Email já cadastrado."
                        else -> "Erro ao cadastrar: ${response.message()}"
                    }
                    Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_LONG).show()
                    btnCadastrar.isEnabled = true
                    btnCadastrar.text = originalButtonText
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@RegisterActivity,
                    "Erro ao cadastrar: ${e.message ?: "Tente novamente"}",
                    Toast.LENGTH_LONG
                ).show()
                btnCadastrar.isEnabled = true
                btnCadastrar.text = originalButtonText
            }
        }
    }
}
