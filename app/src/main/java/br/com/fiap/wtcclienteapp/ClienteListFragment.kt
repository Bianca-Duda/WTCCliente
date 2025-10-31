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
        // val btnCriarAviso = view.findViewById<Button>(R.id.btnCriarAviso) // Comentado
        // val btnCriarCliente = view.findViewById<Button>(R.id.btnCriarCliente) // Não está no layout
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
            // Score removido do filtro
            val status = bundle.getString(FilterDialog.KEY_STATUS).orEmpty()
            val tag = bundle.getString(FilterDialog.KEY_TAG).orEmpty()
            val id = bundle.getString(FilterDialog.KEY_ID).orEmpty().toLongOrNull()
            viewModel.filtrarClientes(Filtros(cpf = cpf.takeIf { it.isNotEmpty() }, score = null, status = status.takeIf { it.isNotEmpty() }, tag = tag.takeIf { it.isNotEmpty() }, id = id))
        }
        
        // Observar mensagens de erro e sucesso
        viewModel.erro.observe(viewLifecycleOwner) { erro ->
            erro?.let {
                android.widget.Toast.makeText(requireContext(), it, android.widget.Toast.LENGTH_LONG).show()
                viewModel.limparMensagens()
            }
        }
        
        viewModel.sucesso.observe(viewLifecycleOwner) { sucesso ->
            sucesso?.let {
                android.widget.Toast.makeText(requireContext(), it, android.widget.Toast.LENGTH_SHORT).show()
                viewModel.limparMensagens()
            }
        }

        btnCriarGrupo.setOnClickListener {
            // Passar lista de clientes para o diálogo
            CreateGroupDialog.newInstance(currentClientes).show(parentFragmentManager, "create_group")
        }
        
        // Observar mensagens de erro e sucesso do ConversaViewModel
        val conversaViewModel = ViewModelProvider(this)[ConversaViewModel::class.java]
        conversaViewModel.erro.observe(viewLifecycleOwner) { erro ->
            erro?.let {
                android.widget.Toast.makeText(requireContext(), it, android.widget.Toast.LENGTH_LONG).show()
                conversaViewModel.limparMensagens()
            }
        }
        
        conversaViewModel.sucesso.observe(viewLifecycleOwner) { sucesso ->
            sucesso?.let {
                android.widget.Toast.makeText(requireContext(), it, android.widget.Toast.LENGTH_SHORT).show()
                conversaViewModel.limparMensagens()
            }
        }
        
        // Criar Cliente - Não está no layout atual
        /*
        btnCriarCliente.setOnClickListener {
            CreateClienteDialog.newInstance().show(parentFragmentManager, "create_cliente")
        }
        */

        btnMeusGrupos.setOnClickListener {
            // Mostrar lista de conversas da API
            ConversaListDialog().show(parentFragmentManager, "conversa_list")
        }

        // Criar Aviso - Comentado
        /*
        btnCriarAviso.setOnClickListener {
            val clientIds = currentClientes.map { it.id.toString() }
            val clientNames = currentClientes.map { it.nome }
            val groups = GroupStore.listGroups()
            val groupIds = groups.map { it.id }
            val groupNames = groups.map { it.name }
            CreateNoticeDialog.newInstance(clientIds, clientNames, groupIds, groupNames)
                .show(parentFragmentManager, "create_notice")
        }
        */

        // Carrega inicial
        viewModel.filtrarClientes(Filtros())
    }
}


