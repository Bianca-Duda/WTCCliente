package br.com.fiap.wtcclienteapp

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider

class QuickNoteDialog : DialogFragment() {
    private lateinit var viewModel: AnotacaoViewModel
    private var clienteId: Long = 0
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel = ViewModelProvider(this)[AnotacaoViewModel::class.java]
        clienteId = requireArguments().getLong(ARG_CLIENTE_ID, 0)
        
        val input = EditText(requireContext()).apply {
            hint = "Digite sua anotação sobre o cliente..."
            minLines = 4
        }
        
        // Carregar anotação local primeiro (fallback rápido)
        val noteLocal = NotesStorage.get(requireContext(), clienteId.toString())
        if (noteLocal.isNotEmpty()) {
            input.setText(noteLocal)
        }
        
        // Buscar anotações existentes do cliente da API
        viewModel.listarAnotacoesPorCliente(clienteId)
        
        // Observar anotações para preencher o campo com a última anotação da API
        viewModel.anotacoes.observe(this) { anotacoes ->
            if (anotacoes.isNotEmpty()) {
                val ultimaAnotacao = anotacoes.maxByOrNull { it.dataCriacao ?: "" }
                ultimaAnotacao?.texto?.let { texto ->
                    if (input.text.toString().isEmpty() || texto != noteLocal) {
                        input.setText(texto)
                    }
                }
            }
        }
        
        // Observar mensagens de erro e sucesso
        viewModel.erro.observe(this) { erro ->
            erro?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.limparMensagens()
            }
        }
        
        viewModel.sucesso.observe(this) { sucesso ->
            sucesso?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.limparMensagens()
                dismiss()
            }
        }
        
        return AlertDialog.Builder(requireContext())
            .setTitle("Anotação do Cliente")
            .setView(input)
            .setPositiveButton("Salvar") { _, _ ->
                val texto = input.text.toString().trim()
                if (texto.isEmpty()) {
                    Toast.makeText(requireContext(), "Digite uma anotação antes de salvar", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                // Salvar via API
                viewModel.criarAnotacao(clienteId, texto)
                // Também salvar localmente como backup
                NotesStorage.put(requireContext(), clienteId.toString(), texto)
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    companion object {
        private const val ARG_CLIENTE_ID = "cliente_id"
        fun newInstance(clientId: String): QuickNoteDialog {
            val d = QuickNoteDialog()
            d.arguments = Bundle().apply { 
                putLong(ARG_CLIENTE_ID, clientId.toLongOrNull() ?: 0)
            }
            return d
        }
    }
}


