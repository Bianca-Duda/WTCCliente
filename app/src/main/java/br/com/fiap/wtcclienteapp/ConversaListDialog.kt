package br.com.fiap.wtcclienteapp

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.wtcclienteapp.network.model.ConversaResponse

class ConversaListDialog : DialogFragment() {
    private lateinit var viewModel: ConversaViewModel
    private lateinit var adapter: ConversaAdapter
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel = ViewModelProvider(this)[ConversaViewModel::class.java]
        
        // Criar adapter primeiro
        adapter = ConversaAdapter(emptyList()) { conversa ->
            // Abrir a conversa/grupo
            if (conversa.ehGrupo == true) {
                // Abrir como grupo
                startActivity(Intent(requireContext(), GroupChatActivity::class.java).apply {
                    putExtra(GroupChatActivity.EXTRA_GROUP_ID, conversa.id.toString())
                    putExtra(GroupChatActivity.EXTRA_GROUP_NAME, conversa.titulo ?: "Grupo")
                })
            } else {
                // Abrir como chat 1:1 (pegar primeiro participante que não seja o operador logado)
                val participante = conversa.participantes?.firstOrNull { it.tipo != "OPERADOR" }
                if (participante != null) {
                    startActivity(Intent(requireContext(), ChatActivity::class.java).apply {
                        putExtra(ChatActivity.EXTRA_PEER_ID, participante.id.toString())
                        putExtra(ChatActivity.EXTRA_PEER_NAME, participante.nome)
                    })
                }
            }
            dismiss()
        }
        
        val recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ConversaListDialog.adapter
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                // Definir altura máxima
                height = (resources.displayMetrics.heightPixels * 0.6).toInt()
            }
        }
        
        // Observar lista de conversas
        viewModel.conversas.observe(this) { conversas ->
            adapter.submitList(conversas)
        }
        
        // Observar erros
        viewModel.erro.observe(this) { erro ->
            erro?.let {
                android.widget.Toast.makeText(requireContext(), it, android.widget.Toast.LENGTH_LONG).show()
                viewModel.limparMensagens()
            }
        }
        
        // Carregar conversas
        viewModel.listarConversas()
        
        return AlertDialog.Builder(requireContext())
            .setTitle("Minhas Conversas")
            .setView(recyclerView)
            .setNegativeButton("Fechar", null)
            .create()
    }
}

class ConversaAdapter(
    private var items: List<ConversaResponse>,
    private val onItemClick: (ConversaResponse) -> Unit
) : RecyclerView.Adapter<ConversaAdapter.ViewHolder>() {
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(android.R.id.text1)
        val subtitle: TextView = view.findViewById(android.R.id.text2)
    }
    
    fun submitList(newItems: List<ConversaResponse>) {
        items = newItems
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root = android.view.LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(root)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val tipo = if (item.ehGrupo == true) "Grupo" else "Conversa 1:1"
        val participantesCount = item.participantes?.size ?: 0
        
        holder.title.text = item.titulo ?: "Sem título"
        
        // Construir informações dos participantes
        val participantesNomes = item.participantes?.map { it.nome }?.joinToString(", ") ?: ""
        val subtituloText = buildString {
            append(tipo)
            append(" • $participantesCount participante(s)")
            if (participantesNomes.isNotEmpty()) {
                append("\nParticipantes: $participantesNomes")
            }
            if (item.ultimaMensagem != null && item.ultimaMensagem.isNotEmpty()) {
                append("\nÚltima mensagem: ${item.ultimaMensagem}")
            }
            if (item.dataCriacao != null) {
                // Formatar data de forma simples (extrair apenas data e hora básica)
                val dataFormatada = try {
                    val partes = item.dataCriacao.split("T")
                    if (partes.size >= 2) {
                        val data = partes[0].split("-").reversed().joinToString("/")
                        val hora = partes[1].substringBefore(".").substring(0, 5)
                        "$data $hora"
                    } else {
                        item.dataCriacao
                    }
                } catch (e: Exception) {
                    item.dataCriacao
                }
                append("\nCriada em: $dataFormatada")
            }
        }
        
        holder.subtitle.text = subtituloText
        holder.subtitle.maxLines = 5
        
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }
    
    override fun getItemCount(): Int = items.size
}
