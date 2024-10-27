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
    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val itemsRecyclerView: RecyclerView = itemView.findViewById(R.id.itemsRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        val category = categories[position]
        holder.categoryName.text = category.name
        holder.itemsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.itemsRecyclerView.adapter = ItemAdapter(category.items, onItemClicked)
    }

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

    override fun getItemCount(): Int = categories.size
}