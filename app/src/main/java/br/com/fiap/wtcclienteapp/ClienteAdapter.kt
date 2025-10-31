package br.com.fiap.wtcclienteapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.wtcclienteapp.model.Cliente
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ClienteAdapter(private var items: List<Cliente>) : RecyclerView.Adapter<ClienteAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(android.R.id.text1)
        val subtitle: TextView = view.findViewById(android.R.id.text2)
        val btnNote: ImageButton = ImageButton(view.context).apply { setImageResource(android.R.drawable.ic_menu_edit) }
    }

    fun submit(newItems: List<Cliente>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(root).also { vh ->
            val ctx = parent.context
            vh.title.setTextColor(ContextCompat.getColor(ctx, R.color.text_dark))
            vh.subtitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_hint))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.nome
        val tagsStr = if (item.tags.isNotEmpty()) " | Tags: ${item.tags.joinToString(", ")}" else ""
        holder.subtitle.text = "CPF: ${item.cpf} | Score: ${item.scoreCrm} | Status: ${item.status.descricao}$tagsStr"
        holder.itemView.setOnClickListener {
            val ctx = holder.itemView.context
            val clientId = item.id.toString()
            
            // Buscar anotações da API e exibir diálogo
            val viewModel = androidx.lifecycle.ViewModelProvider(
                (ctx as AppCompatActivity)
            )[AnotacaoViewModel::class.java]
            
            // Fallback local imediato
            val noteLocal = NotesStorage.get(ctx, clientId)
            
            // Função para criar e mostrar o diálogo
            fun mostrarDialogo(noteText: String) {
                val message = if (noteText.isBlank()) "Sem anotações." else "Anotação:\n$noteText"
                
                AlertDialog.Builder(ctx)
                    .setTitle(item.nome)
                    .setMessage(message)
                    .setPositiveButton("Enviar mensagem") { _, _ ->
                        ctx.startActivity(Intent(ctx, ChatActivity::class.java).apply {
                            putExtra(ChatActivity.EXTRA_PEER_ID, clientId)
                            putExtra(ChatActivity.EXTRA_PEER_NAME, item.nome)
                        })
                    }
                    .setNeutralButton("Editar Cliente") { _, _ ->
                        (ctx as AppCompatActivity).let { act ->
                            EditClienteDialog.newInstance(item)
                                .show(act.supportFragmentManager, "edit_cliente")
                        }
                    }
                    .setNegativeButton("Criar anotações") { _, _ ->
                        (ctx as AppCompatActivity).let { act ->
                            QuickNoteDialog.newInstance(clientId)
                                .show(act.supportFragmentManager, "note")
                        }
                    }
                    .show()
            }
            
            // Mostrar diálogo imediatamente com anotação local (se houver)
            if (noteLocal.isNotBlank()) {
                mostrarDialogo(noteLocal)
            } else {
                mostrarDialogo("")
            }
            
            // Buscar da API e atualizar se houver anotação mais recente
            viewModel.listarAnotacoesPorCliente(item.id)
            viewModel.anotacoes.observe((ctx as AppCompatActivity)) { anotacoes ->
                val ultimaAnotacao = anotacoes.maxByOrNull { it.dataCriacao ?: "" }
                val noteTextApi = ultimaAnotacao?.texto
                
                // Se houver anotação da API mais recente, atualizar o diálogo
                if (!noteTextApi.isNullOrBlank()) {
                    // Fechar diálogo anterior e mostrar novo se a API retornar algo
                    // Por enquanto, apenas salvar localmente como cache
                    NotesStorage.put(ctx, clientId, noteTextApi)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}


