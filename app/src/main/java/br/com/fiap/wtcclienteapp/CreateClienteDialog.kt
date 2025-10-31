package br.com.fiap.wtcclienteapp

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import br.com.fiap.wtcclienteapp.network.AuthManager

class CreateClienteDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
        }

        val inputCpf = EditText(requireContext()).apply {
            hint = "CPF"
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        
        val inputScore = EditText(requireContext()).apply {
            hint = "Score CRM (0-100)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        
        val spinnerStatus = Spinner(requireContext())
        val statusOptions = arrayOf("ATIVO", "INATIVO", "VIP")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter
        
        val inputObservacoes = EditText(requireContext()).apply {
            hint = "Observações (opcional)"
            minLines = 2
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        
        val inputTags = EditText(requireContext()).apply {
            hint = "Tags (separadas por vírgula, ex: Premium, VIP)"
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        
        container.addView(TextView(requireContext()).apply { text = "CPF:" })
        container.addView(inputCpf)
        container.addView(TextView(requireContext()).apply { text = "Status:" })
        container.addView(spinnerStatus)
        container.addView(TextView(requireContext()).apply { text = "Score CRM:" })
        container.addView(inputScore)
        container.addView(TextView(requireContext()).apply { text = "Observações:" })
        container.addView(inputObservacoes)
        container.addView(TextView(requireContext()).apply { text = "Tags:" })
        container.addView(inputTags)

        return AlertDialog.Builder(requireContext())
            .setTitle("Criar Cliente")
            .setView(container)
            .setPositiveButton("Criar") { _, _ ->
                val cpf = inputCpf.text.toString().trim()
                val status = spinnerStatus.selectedItem.toString()
                val scoreStr = inputScore.text.toString()
                val score = scoreStr.toIntOrNull() ?: 0
                val observacoes = inputObservacoes.text.toString().trim().takeIf { it.isNotEmpty() }
                val tagsStr = inputTags.text.toString().trim()
                val tags = tagsStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                
                if (cpf.isEmpty()) {
                    Toast.makeText(requireContext(), "CPF é obrigatório", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                // Buscar o ID do usuário logado (operador)
                val usuarioId = AuthManager.getUserId()
                if (usuarioId == null) {
                    Toast.makeText(requireContext(), "Erro: Usuário não logado", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                // Chamar o ViewModel para criar o cliente
                val viewModel = (requireActivity() as? androidx.fragment.app.FragmentActivity)
                    ?.let { androidx.lifecycle.ViewModelProvider(it)[ClienteViewModel::class.java] }
                
                viewModel?.criarCliente(usuarioId, cpf, status, score, observacoes, tags)
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }
    
    companion object {
        fun newInstance(): CreateClienteDialog {
            return CreateClienteDialog()
        }
    }
}
