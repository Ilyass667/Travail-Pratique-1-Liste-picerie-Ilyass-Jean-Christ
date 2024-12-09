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

// Adapter pour les items du panier
class PanierItemAdapter(val items: MutableList<PanierItem>, private val onItemClicked: (PanierItem) -> Unit) : RecyclerView.Adapter<PanierItemAdapter.PanierItemViewHolder>() {

    // ViewHolder pour les items du panier
    class PanierItemViewHolder(itemView: View, private val onItemClicked: (PanierItem) -> Unit, private val items: MutableList<PanierItem>) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete2)

        init {
            // Définir le clic sur l'item
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(items[position])
                }
            }

            // Définir le clic sur le bouton de suppression
            buttonDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position]
                    CoroutineScope(Dispatchers.IO).launch {
                        // Supprimer l'item du panier
                        val db = Room.databaseBuilder(
                            itemView.context,
                            PanierDatabase::class.java, "panier_db"
                        ).build()
                        db.panierItemDao().delete(item)

                        withContext(Dispatchers.Main) {
                            val mainActivity = (itemView.context as MainActivity)
                            // Supprimer l'item du RecyclerView et notifier l'adapteur
//                            mainActivity.deleteFromRecycleView(item)
                        }
                    }
                }
            }
        }
    }

    // Créer le ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PanierItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_panier, parent, false)
        return PanierItemViewHolder(itemView, onItemClicked, items)
    }

    // Lier les données au ViewHolder
    override fun onBindViewHolder(holder: PanierItemViewHolder, position: Int) {
        val currentItem = items[position]
        holder.textViewName.text = currentItem.name
    }

    // Retourner le nombre d'items
    override fun getItemCount() = items.size
}