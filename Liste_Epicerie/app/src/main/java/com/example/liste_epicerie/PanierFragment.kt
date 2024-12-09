package com.example.liste_epicerie

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PanierFragment : Fragment() {

    companion object {
        fun newInstance() = PanierFragment()
    }

    private val viewModel: PanierViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_panier, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerViewPanier: RecyclerView = view.findViewById(R.id.recyclerViewPanier)
        recyclerViewPanier.layoutManager = LinearLayoutManager(context)

        CoroutineScope(Dispatchers.IO).launch {
            val panierItemList = viewModel.getAllPanierItems()
            withContext(Dispatchers.Main) {
                recyclerViewPanier.adapter = PanierItemAdapter(panierItemList.toMutableList()) { panierItem ->
                    viewModel.moveToCategories(panierItem)
                }
            }
        }
    }
}