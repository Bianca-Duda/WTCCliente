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
        val inputScore = EditText(requireContext()).apply { hint = "Score"; inputType = android.text.InputType.TYPE_CLASS_NUMBER }
        val inputStatus = EditText(requireContext()).apply { hint = "Status" }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
            addView(inputCpf)
            addView(inputScore)
            addView(inputStatus)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Pesquisa de Clientes")
            .setView(container)
            .setPositiveButton("Aplicar") { _, _ ->
                val cpf = inputCpf.text.toString()
                val score = inputScore.text.toString()
                val status = inputStatus.text.toString()
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(
                        KEY_CPF to cpf,
                        KEY_SCORE to score,
                        KEY_STATUS to status
                    )
                )
            }
            .setNeutralButton("Limpar") { _, _ ->
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(
                        KEY_CPF to "",
                        KEY_SCORE to "",
                        KEY_STATUS to ""
                    )
                )
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    companion object {
        const val REQUEST_KEY = "filters_request"
        const val KEY_CPF = "cpf"
        const val KEY_SCORE = "score"
        const val KEY_STATUS = "status"
    }
}
