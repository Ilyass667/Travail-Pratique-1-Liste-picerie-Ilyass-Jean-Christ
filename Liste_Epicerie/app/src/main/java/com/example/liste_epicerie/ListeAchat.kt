package com.example.liste_epicerie

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.liste_epicerie.data.Category
import com.example.liste_epicerie.data.Item
import com.example.liste_epicerie.data.PanierItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

class ListeAchat : Fragment() {

    companion object {
        fun newInstance() = ListeAchat()
    }

    private val viewModel: ListeAchatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_liste_achat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        CoroutineScope(Dispatchers.IO).launch {
            val itemList = viewModel.getAllItems()
            withContext(Dispatchers.Main) {
                val categories = itemList.groupBy { it.category }.map { Category(it.key, it.value.toMutableList()) }.toMutableList()
                recyclerView.adapter = CategoryAdapter(categories) { item ->
                    moveToPanier(item)
                }
            }
        }
    }

    // Move item to panier
private fun moveToPanier(item: Item) {
    viewModel.moveToPanier(item)

    // Remove item from the item RecyclerView
    val itemAdapter = view?.findViewById<RecyclerView>(R.id.recyclerView)?.adapter as? CategoryAdapter
    val category = itemAdapter?.categories?.find { it.items.contains(item) }
//    category?.items?.remove(item)
//    itemAdapter?.notifyDataSetChanged()
//    itemAdapter?.checkAndRemoveEmptyCategories()

    // Add item to the panier RecyclerView

        val panierView = layoutInflater.inflate(R.layout.fragment_panier, null)
        val panierRecyclerView  = panierView.findViewById<RecyclerView>(R.id.recyclerViewPanier)
        val panierAdapter = panierView.findViewById<RecyclerView>(R.id.recyclerViewPanier)?.adapter as? PanierItemAdapter
    val panierItem = PanierItem(
        name = item.name,
        quantity = item.quantity,
        category = item.category,
        imageUri = item.imageUri
    )
        Log.d("ListeAchat", "moveToPanier: $panierRecyclerView")
    panierAdapter?.items?.add(panierItem)
    panierAdapter?.notifyDataSetChanged()

}
}