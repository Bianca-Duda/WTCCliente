package br.com.fiap.wtcclienteapp

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class QuickNoteDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val input = EditText(requireContext())
        val clientId = requireArguments().getString(ARG_ID) ?: ""
        input.setText(NotesStorage.get(requireContext(), clientId))
        return AlertDialog.Builder(requireContext())
            .setTitle("Anotação Rápida")
            .setView(input)
            .setPositiveButton("Salvar") { _, _ ->
                NotesStorage.put(requireContext(), clientId, input.text.toString())
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    companion object {
        private const val ARG_ID = "id"
        fun newInstance(clientId: String): QuickNoteDialog {
            val d = QuickNoteDialog()
            d.arguments = Bundle().apply { putString(ARG_ID, clientId) }
            return d
        }
    }
}


