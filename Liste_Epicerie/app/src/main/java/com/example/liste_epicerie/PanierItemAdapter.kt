package com.example.liste_epicerie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.liste_epicerie.data.Item
import com.example.liste_epicerie.data.PanierItem

class PanierItemAdapter(val items: MutableList<PanierItem>) : RecyclerView.Adapter<PanierItemAdapter.PanierItemViewHolder>() {

    class PanierItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.Panier)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PanierItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.panier, parent, false)
        return PanierItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: PanierItemViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item.name
    }

    override fun getItemCount(): Int = items.size
}