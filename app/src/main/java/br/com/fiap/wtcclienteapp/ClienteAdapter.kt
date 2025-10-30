package br.com.fiap.wtcclienteapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.wtcclienteapp.model.Cliente

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
        holder.itemView.setOnLongClickListener {
            QuickNoteDialog.newInstance(item.id.toString()).show((holder.itemView.context as androidx.appcompat.app.AppCompatActivity).supportFragmentManager, "note")
            true
        }
    }

    override fun getItemCount(): Int = items.size
}


