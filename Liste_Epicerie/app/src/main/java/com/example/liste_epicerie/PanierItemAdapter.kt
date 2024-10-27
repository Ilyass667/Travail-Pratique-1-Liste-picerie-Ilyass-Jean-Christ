package com.example.liste_epicerie

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.liste_epicerie.data.Item
import com.example.liste_epicerie.data.ItemDatabase
import com.example.liste_epicerie.data.PanierDatabase
import com.example.liste_epicerie.data.PanierItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PanierItemAdapter(val items: MutableList<PanierItem>, private val onItemClicked: (PanierItem) -> Unit) : RecyclerView.Adapter<PanierItemAdapter.PanierItemViewHolder>() {

        class PanierItemViewHolder(itemView: View, private val onItemClicked: (PanierItem) -> Unit, private val items: MutableList<PanierItem>) : RecyclerView.ViewHolder(itemView) {
            val textViewName: TextView = itemView.findViewById(R.id.textViewName)
            val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete2)

            init {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClicked(items[position])
                    }
                }

            buttonDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position]
                    CoroutineScope(Dispatchers.IO).launch {
                        // Delete item from the panier database
                        val db = Room.databaseBuilder(
                            itemView.context,
                            PanierDatabase::class.java, "panier_db"
                        ).build()
                        db.panierItemDao().delete(item)

                        withContext(Dispatchers.Main) {
                            val mainActivity = (itemView.context as MainActivity)
                            // Remove item from the panier RecyclerView and notify adapter
                            mainActivity.deleteFromRecycleView(item)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PanierItemViewHolder {
val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_panier, parent, false)
return PanierItemViewHolder(itemView, onItemClicked, items)
    }

    override fun onBindViewHolder(holder: PanierItemViewHolder, position: Int) {
        val currentItem = items[position]
        holder.textViewName.text = currentItem.name
    }

    override fun getItemCount() = items.size
}