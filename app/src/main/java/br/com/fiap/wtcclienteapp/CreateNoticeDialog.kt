package br.com.fiap.wtcclienteapp

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class CreateNoticeDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = requireArguments()
        val clientIds = args.getStringArrayList(ARG_CLIENT_IDS) ?: arrayListOf()
        val clientNames = args.getStringArrayList(ARG_CLIENT_NAMES) ?: arrayListOf()
        val groupIds = args.getStringArrayList(ARG_GROUP_IDS) ?: arrayListOf()
        val groupNames = args.getStringArrayList(ARG_GROUP_NAMES) ?: arrayListOf()

        val titleInput = EditText(requireContext()).apply { hint = "Título" }
        val messageInput = EditText(requireContext()).apply {
            hint = "Mensagem"
            minLines = 3
        }
        val dateInput = EditText(requireContext()).apply { hint = "Data (opcional)" }

        val selectedClientFlags = BooleanArray(clientIds.size)
        val selectedGroupFlags = BooleanArray(groupIds.size)

        val clientsSummary = TextView(requireContext()).apply { text = "Clientes selecionados: 0" }
        val groupsSummary = TextView(requireContext()).apply { text = "Grupos selecionados: 0" }

        val pickClientsBtn = Button(requireContext()).apply {
            text = "Selecionar Clientes"
            setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Selecionar Clientes")
                    .setMultiChoiceItems(clientNames.toTypedArray(), selectedClientFlags) { _, which, isChecked ->
                        selectedClientFlags[which] = isChecked
                    }
                    .setPositiveButton("OK") { _, _ ->
                        val count = selectedClientFlags.count { it }
                        clientsSummary.text = "Clientes selecionados: $count"
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }

        val pickGroupsBtn = Button(requireContext()).apply {
            text = "Selecionar Grupos"
            setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Selecionar Grupos")
                    .setMultiChoiceItems(groupNames.toTypedArray(), selectedGroupFlags) { _, which, isChecked ->
                        selectedGroupFlags[which] = isChecked
                    }
                    .setPositiveButton("OK") { _, _ ->
                        val count = selectedGroupFlags.count { it }
                        groupsSummary.text = "Grupos selecionados: $count"
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
            addView(titleInput, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            addView(messageInput, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            addView(dateInput, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            addView(pickClientsBtn)
            addView(clientsSummary)
            addView(pickGroupsBtn)
            addView(groupsSummary)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Criar Aviso")
            .setView(container)
            .setPositiveButton("Enviar") { _, _ ->
                val selectedClientIds = clientIds.filterIndexed { index, _ -> selectedClientFlags.getOrNull(index) == true }
                val selectedGroupIds = groupIds.filterIndexed { index, _ -> selectedGroupFlags.getOrNull(index) == true }
                val notice = Notice(
                    title = titleInput.text.toString().ifBlank { "Aviso" },
                    content = messageInput.text.toString(),
                    date = dateInput.text.toString().ifBlank { null },
                    recipientClientIds = selectedClientIds,
                    recipientGroupIds = selectedGroupIds
                )
                NoticeBus.send(notice)
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    companion object {
        private const val ARG_CLIENT_IDS = "client_ids"
        private const val ARG_CLIENT_NAMES = "client_names"
        private const val ARG_GROUP_IDS = "group_ids"
        private const val ARG_GROUP_NAMES = "group_names"

        fun newInstance(clientIds: List<String>, clientNames: List<String>, groupIds: List<String>, groupNames: List<String>): CreateNoticeDialog {
            val d = CreateNoticeDialog()
            d.arguments = Bundle().apply {
                putStringArrayList(ARG_CLIENT_IDS, ArrayList(clientIds))
                putStringArrayList(ARG_CLIENT_NAMES, ArrayList(clientNames))
                putStringArrayList(ARG_GROUP_IDS, ArrayList(groupIds))
                putStringArrayList(ARG_GROUP_NAMES, ArrayList(groupNames))
            }
            return d
        }
    }
}
