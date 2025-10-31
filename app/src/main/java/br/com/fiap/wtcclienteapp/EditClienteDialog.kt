package br.com.fiap.wtcclienteapp

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import br.com.fiap.wtcclienteapp.model.Cliente

class EditClienteDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val cliente = arguments?.getSerializable(ARG_CLIENTE) as? Cliente
            ?: return AlertDialog.Builder(requireContext())
                .setMessage("Cliente não encontrado")
                .setPositiveButton("OK", null)
                .create()

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
        }

        val inputCpf = EditText(requireContext()).apply {
            hint = "CPF"
            setText(cliente.cpf)
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
        val statusIndex = statusOptions.indexOf(cliente.status.descricao.uppercase())
        if (statusIndex >= 0) {
            spinnerStatus.setSelection(statusIndex)
        }
        
        val inputScore = EditText(requireContext()).apply {
            hint = "Score CRM (0-100)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(cliente.scoreCrm.toString())
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        
        val inputObservacoes = EditText(requireContext()).apply {
            hint = "Observações (opcional)"
            minLines = 2
            setText(cliente.observacoes)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        
        val inputTags = EditText(requireContext()).apply {
            hint = "Tags (separadas por vírgula)"
            setText(cliente.tags.joinToString(", "))
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
            .setTitle("Editar Cliente: ${cliente.nome}")
            .setView(container)
            .setPositiveButton("Salvar") { _, _ ->
                val cpf = inputCpf.text.toString().trim()
                val status = spinnerStatus.selectedItem.toString()
                val scoreStr = inputScore.text.toString()
                val score = scoreStr.toIntOrNull()
                val observacoes = inputObservacoes.text.toString().trim().takeIf { it.isNotEmpty() }
                val tagsStr = inputTags.text.toString().trim()
                val tags = tagsStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                
                // Chamar o ViewModel para atualizar o cliente
                val viewModel = (requireActivity() as? androidx.fragment.app.FragmentActivity)
                    ?.let { androidx.lifecycle.ViewModelProvider(it)[ClienteViewModel::class.java] }
                
                viewModel?.atualizarCliente(cliente.id, cpf, status, score, observacoes, tags)
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }
    
    companion object {
        private const val ARG_CLIENTE = "cliente"
        fun newInstance(cliente: Cliente): EditClienteDialog {
            val dialog = EditClienteDialog()
            dialog.arguments = Bundle().apply {
                putSerializable(ARG_CLIENTE, cliente)
            }
            return dialog
        }
    }
}
