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
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.nome
        holder.subtitle.text = "CPF: ${item.cpf} | Score: ${item.scoreCrm} | Status: ${item.status}"
        holder.itemView.setOnClickListener {
            val ctx = holder.itemView.context
            val clientId = item.id.toString()
            val note = NotesStorage.get(ctx, clientId)
            val message = if (note.isBlank()) "Sem anotações." else "Anotação:\n$note"

            AlertDialog.Builder(ctx)
                .setTitle(item.nome)
                .setMessage(message)
                .setPositiveButton("Enviar mensagem") { _, _ ->
                    ctx.startActivity(Intent(ctx, ChatActivity::class.java).apply {
                        putExtra(ChatActivity.EXTRA_PEER_ID, clientId)
                        putExtra(ChatActivity.EXTRA_PEER_NAME, item.nome)
                    })
                }
                .setNeutralButton("Criar anotações") { _, _ ->
                    (ctx as AppCompatActivity).let { act ->
                        QuickNoteDialog.newInstance(clientId)
                            .show(act.supportFragmentManager, "note")
                    }
                }
                .setNegativeButton("Fechar", null)
                .show()
        }
    }

    override fun getItemCount(): Int = items.size
}


