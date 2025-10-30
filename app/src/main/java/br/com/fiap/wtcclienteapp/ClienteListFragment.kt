package br.com.fiap.wtcclienteapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ClienteListFragment : Fragment() {
    private lateinit var viewModel: ClienteViewModel
    private lateinit var adapter: ClienteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_lista_clientes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ClienteViewModel::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerClientes)
        val btnFiltrar = view.findViewById<Button>(R.id.btnFiltrar)
        val btnLimpar = view.findViewById<Button>(R.id.btnLimpar)
        val btnCampanha = Button(requireContext()).apply {
            text = "Campanha Express"
            setOnClickListener { CampanhaExpressDialog().show(parentFragmentManager, "campanha") }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ClienteAdapter(emptyList())
        recyclerView.adapter = adapter

        viewModel.clientes.observe(viewLifecycleOwner) { lista ->
            adapter.submit(lista)
        }

        btnFiltrar.setOnClickListener {
            val tag = view.findViewById<EditText>(R.id.inputTag).text.toString()
            val score = view.findViewById<EditText>(R.id.inputScore).text.toString().toIntOrNull()
            val status = view.findViewById<EditText>(R.id.inputStatus).text.toString()
            viewModel.filtrarClientes(Filtros(tag = tag, score = score, status = status))
        }

        btnLimpar.setOnClickListener {
            view.findViewById<EditText>(R.id.inputTag).setText("")
            view.findViewById<EditText>(R.id.inputScore).setText("")
            view.findViewById<EditText>(R.id.inputStatus).setText("")
            viewModel.filtrarClientes(Filtros())
        }

        // Carrega inicial
        viewModel.filtrarClientes(Filtros())
    }
}


