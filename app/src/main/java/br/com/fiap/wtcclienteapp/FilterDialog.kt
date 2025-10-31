package br.com.fiap.wtcclienteapp

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment

class FilterDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inputCpf = EditText(requireContext()).apply { hint = "CPF" }
        // Score removido do filtro
        val inputStatus = EditText(requireContext()).apply { hint = "Status" }
        val inputTag = EditText(requireContext()).apply { hint = "Tag (ex: VIP)" }
        val inputId = EditText(requireContext()).apply { hint = "ID do Cliente"; inputType = android.text.InputType.TYPE_CLASS_NUMBER }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
            addView(inputCpf)
            // inputScore removido
            addView(inputStatus)
            addView(inputTag)
            addView(inputId)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Pesquisa de Clientes")
            .setView(container)
            .setPositiveButton("Aplicar") { _, _ ->
                val cpf = inputCpf.text.toString()
                // Score removido
                val status = inputStatus.text.toString()
                val tag = inputTag.text.toString()
                val id = inputId.text.toString()
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(
                        KEY_CPF to cpf,
                        // KEY_SCORE removido
                        KEY_STATUS to status,
                        KEY_TAG to tag,
                        KEY_ID to id
                    )
                )
            }
            .setNeutralButton("Limpar") { _, _ ->
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(
                        KEY_CPF to "",
                        // KEY_SCORE removido
                        KEY_STATUS to "",
                        KEY_TAG to "",
                        KEY_ID to ""
                    )
                )
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    companion object {
        const val REQUEST_KEY = "filters_request"
        const val KEY_CPF = "cpf"
        // KEY_SCORE removido
        const val KEY_STATUS = "status"
        const val KEY_TAG = "tag"
        const val KEY_ID = "id"
    }
}
