package br.com.fiap.wtcclienteapp

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class CreateGroupDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inputName = EditText(requireContext()).apply { hint = "Nome do grupo" }
        val inputMembers = EditText(requireContext()).apply { hint = "Nomes dos clientes (separados por vírgula)" }
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
            addView(inputName)
            addView(inputMembers)
        }
        return AlertDialog.Builder(requireContext())
            .setTitle("Criar grupo")
            .setView(container)
            .setPositiveButton("Criar") { _, _ ->
                val name = inputName.text.toString().ifBlank { "Grupo" }
                val members = inputMembers.text.toString().split(',').map { it.trim() }.filter { it.isNotEmpty() }
                GroupStore.createGroup(name, memberIds = members, memberNames = members)
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }
}


