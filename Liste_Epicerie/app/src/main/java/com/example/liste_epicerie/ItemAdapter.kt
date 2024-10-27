package com.example.liste_epicerie

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.liste_epicerie.data.Item
import java.io.File

class ItemAdapter(private val itemList: List<Item>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View, private val itemList: List<Item>) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewQuantity: TextView = itemView.findViewById(R.id.textViewQuantity)
        val imageViewItem: ImageView = itemView.findViewById(R.id.imageViewItem)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = itemList[position]
                    val intent = Intent(itemView.context, ModificationItem::class.java).apply {
                        putExtra("ITEM_ID", item.id)
                    }
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ItemViewHolder(itemView, itemList)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.textViewName.text = currentItem.name
        holder.textViewQuantity.text = if (currentItem.quantity > 1) "(${currentItem.quantity})" else ""
        val uri: Uri? = currentItem.imageUri?.let { Uri.parse(it) }
        if(currentItem.imageUri != null){
            holder.imageViewItem.setImageURI(uri)
        }

    }

    override fun getItemCount() = itemList.size
}

