package com.example.liste_epicerie

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

// Adaptateur pour les éléments
class ItemAdapter(private val itemList: MutableList<Item>, private val onItemClicked: (Item) -> Unit) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    // ViewHolder pour les éléments
    class ItemViewHolder(private val itemList: MutableList<Item>, itemView: View, private val onItemClicked: (Item) -> Unit, private val adapter: ItemAdapter) : RecyclerView.ViewHolder(itemView)  {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewQuantity: TextView = itemView.findViewById(R.id.textViewQuantity)
        val imageViewItem: ImageView = itemView.findViewById(R.id.imageViewItem)
        val buttonAction: Button = itemView.findViewById(R.id.buttonAction)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)

        init {
            // Gestion du clic sur l'élément
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = itemList[position]
                    onItemClicked(item)
                    textViewName.paintFlags = textViewName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
            }

            // Gestion du clic sur le bouton d'action
            buttonAction.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = itemList[position]
                    val intent = Intent(itemView.context, ModificationItem::class.java).apply {
                        putExtra("ITEM_ID", item.id)
                    }
                    itemView.context.startActivity(intent)
                }
            }

            // Gestion du clic sur le bouton de suppression
            buttonDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = itemList[position]
                    CoroutineScope(Dispatchers.IO).launch {
                        // Retirer l'item de la base de données
                        val db = Room.databaseBuilder(
                            itemView.context,
                            ItemDatabase::class.java, "item_db"
                        ).build()
                        db.itemDao().delete(item)

                        withContext(Dispatchers.Main) {
                            // Mettre à jour la liste et l'adaptateur
                            itemList.removeAt(position)
                            adapter.notifyItemRemoved(position)
                            val categoryAdapter = (itemView.context as MainActivity).findViewById<RecyclerView>(R.id.recyclerView).adapter as CategoryAdapter
                            categoryAdapter.checkAndRemoveEmptyCategories()
                        }
                    }
                }
            }
        }
    }

    // Crée un nouveau ViewHolder pour un élément
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ItemViewHolder(itemList, itemView, onItemClicked, this)
    }

    // Lie les données d'un élément au ViewHolder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.textViewName.text = currentItem.name
        holder.textViewQuantity.text = if (currentItem.quantity > 1) "(${currentItem.quantity})" else ""
        val uri: Uri? = currentItem.imageUri?.let { Uri.parse(it) }
        if (currentItem.imageUri != null) {
            holder.imageViewItem.setImageURI(uri)
        }
    }

    // Supprime un élément de la liste
    fun removeItem(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    // Retourne le nombre d'éléments
    override fun getItemCount() = itemList.size
}