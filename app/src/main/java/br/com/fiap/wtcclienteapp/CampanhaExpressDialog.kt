package br.com.fiap.wtcclienteapp

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class CampanhaExpressDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inputTitle = EditText(requireContext()).apply { hint = "Título" }
        val inputContent = EditText(requireContext()).apply { hint = "Conteúdo" }

        val container = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
            addView(inputTitle)
            addView(inputContent)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Campanha Express")
            .setView(container)
            .setPositiveButton("Enviar") { _, _ ->
                CampaignBus.send(Campaign(inputTitle.text.toString(), inputContent.text.toString()))
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }
}


