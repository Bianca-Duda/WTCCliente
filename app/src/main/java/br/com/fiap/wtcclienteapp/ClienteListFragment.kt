package br.com.fiap.wtcclienteapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ClienteListFragment : Fragment() {
    private lateinit var viewModel: ClienteViewModel
    private lateinit var adapter: ClienteAdapter
    private var currentClientes: List<br.com.fiap.wtcclienteapp.model.Cliente> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_lista_clientes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ClienteViewModel::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerClientes)
        val btnCriarGrupo = view.findViewById<Button>(R.id.btnCriarGrupo)
        val btnCriarAviso = view.findViewById<Button>(R.id.btnCriarAviso)
        val btnMeusGrupos = view.findViewById<Button>(R.id.btnMeusGrupos)
        val btnPesquisa = view.findViewById<Button>(R.id.btnPesquisa)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ClienteAdapter(emptyList())
        recyclerView.adapter = adapter

        viewModel.clientes.observe(viewLifecycleOwner) { lista ->
            currentClientes = lista
            adapter.submit(lista)
        }

        btnPesquisa.setOnClickListener {
            FilterDialog().show(parentFragmentManager, "filter_dialog")
        }

        parentFragmentManager.setFragmentResultListener(FilterDialog.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            val cpf = bundle.getString(FilterDialog.KEY_CPF).orEmpty()
            val score = bundle.getString(FilterDialog.KEY_SCORE).orEmpty().toIntOrNull()
            val status = bundle.getString(FilterDialog.KEY_STATUS).orEmpty()
            viewModel.filtrarClientes(Filtros(cpf = cpf, score = score, status = status))
        }

        btnCriarGrupo.setOnClickListener {
            CreateGroupDialog().show(parentFragmentManager, "create_group")
        }

        btnMeusGrupos.setOnClickListener {
            val groups = GroupStore.listGroups()
            if (groups.isEmpty()) return@setOnClickListener
            val names = groups.map { it.name }.toTypedArray()
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Grupos")
                .setItems(names) { _, which ->
                    val g = groups[which]
                    startActivity(android.content.Intent(requireContext(), GroupChatActivity::class.java).apply {
                        putExtra(GroupChatActivity.EXTRA_GROUP_ID, g.id)
                        putExtra(GroupChatActivity.EXTRA_GROUP_NAME, g.name)
                    })
                }
                .show()
        }

        btnCriarAviso.setOnClickListener {
            val clientIds = currentClientes.map { it.id.toString() }
            val clientNames = currentClientes.map { it.nome }
            val groups = GroupStore.listGroups()
            val groupIds = groups.map { it.id }
            val groupNames = groups.map { it.name }
            CreateNoticeDialog.newInstance(clientIds, clientNames, groupIds, groupNames)
                .show(parentFragmentManager, "create_notice")
        }

        // Carrega inicial
        viewModel.filtrarClientes(Filtros())
    }
}


