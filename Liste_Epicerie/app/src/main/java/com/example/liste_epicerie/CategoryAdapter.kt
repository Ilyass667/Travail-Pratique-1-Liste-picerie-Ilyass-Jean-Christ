package com.example.liste_epicerie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.liste_epicerie.data.Category
import com.example.liste_epicerie.data.Item

class CategoryAdapter(val categories: MutableList<Category>, private val onItemClicked: ((Item) -> Unit)) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    // ViewHolder pour les catégories
    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val itemsRecyclerView: RecyclerView = itemView.findViewById(R.id.itemsRecyclerView)
    }

    // Crée un nouveau ViewHolder pour une catégorie
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    // Lie les données d'une catégorie au ViewHolder
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryName.text = category.name
        holder.itemsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.itemsRecyclerView.adapter = ItemAdapter(category.items, onItemClicked)
    }

    // Vérifie et supprime les catégories vides
    fun checkAndRemoveEmptyCategories() {
        val iterator = categories.iterator()
        while (iterator.hasNext()) {
            val category = iterator.next()
            if (category.items.isEmpty()) {
                val position = categories.indexOf(category)
                iterator.remove()
                notifyItemRemoved(position)
            }
        }
    }

    // Retourne le nombre de catégories
    override fun getItemCount(): Int = categories.size
}