package br.com.fiap.wtcclienteapp

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import br.com.fiap.wtcclienteapp.model.Cliente

class CreateGroupDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val clientes = arguments?.getSerializable(ARG_CLIENTES) as? ArrayList<Cliente> ?: arrayListOf()
        
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
        }
        
        val inputTitulo = EditText(requireContext()).apply {
            hint = "Título da conversa/grupo"
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        
        val switchTipo = Switch(requireContext()).apply {
            text = "É um grupo?"
            isChecked = true
        }
        
        val textViewParticipantes = TextView(requireContext()).apply {
            text = "Selecione os participantes:"
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 8)
            }
        }
        
        // Criar checkboxes para cada cliente
        val clientCheckboxes = clientes.map { cliente ->
            CheckBox(requireContext()).apply {
                text = "${cliente.nome} (ID: ${cliente.id})"
                tag = cliente.id
            }
        }
        
        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                400 // Altura fixa para o scroll
            )
        }
        
        val checkboxContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
        }
        
        clientCheckboxes.forEach { checkbox ->
            checkboxContainer.addView(checkbox)
        }
        
        scrollView.addView(checkboxContainer)
        
        container.addView(TextView(requireContext()).apply { text = "Título:" })
        container.addView(inputTitulo)
        container.addView(switchTipo)
        container.addView(textViewParticipantes)
        container.addView(scrollView)
        
        return AlertDialog.Builder(requireContext())
            .setTitle("Criar Conversa/Grupo")
            .setView(container)
            .setPositiveButton("Criar") { _, _ ->
                val titulo = inputTitulo.text.toString().trim()
                val ehGrupo = switchTipo.isChecked
                val participantesSelecionados = clientCheckboxes
                    .filter { it.isChecked }
                    .mapNotNull { it.tag as? Long }
                
                if (titulo.isEmpty()) {
                    Toast.makeText(requireContext(), "Título é obrigatório", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                if (participantesSelecionados.isEmpty()) {
                    Toast.makeText(requireContext(), "Selecione pelo menos um participante", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                // Chamar o ViewModel para criar a conversa/grupo
                val viewModel = (requireActivity() as? androidx.fragment.app.FragmentActivity)
                    ?.let { androidx.lifecycle.ViewModelProvider(it)[ConversaViewModel::class.java] }
                
                viewModel?.criarConversa(titulo, participantesSelecionados, ehGrupo)
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }
    
    companion object {
        private const val ARG_CLIENTES = "clientes"
        
        fun newInstance(clientes: List<Cliente>): CreateGroupDialog {
            val dialog = CreateGroupDialog()
            dialog.arguments = Bundle().apply {
                putSerializable(ARG_CLIENTES, ArrayList(clientes))
            }
            return dialog
        }
    }
}


