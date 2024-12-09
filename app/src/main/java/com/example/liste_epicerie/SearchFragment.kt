package com.example.liste_epicerie

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView // Assurez-vous d'importer le bon type

class SearchFragment : Fragment() {

    private var onSearchCallback: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Bouton Retour
        val backButton: View = view.findViewById(R.id.back_button)
        backButton.setOnClickListener {
            // Fermer l'activité en cours et revenir à MainActivity
            requireActivity().finish()
        }
        // Bouton Recettes Favorites
        val favoritesButton: View = view.findViewById(R.id.favorites_button)
        favoritesButton.setOnClickListener {
            // Naviguer vers l'activité RecetteFavorite (chat gpt)
            val intent = Intent(requireContext(), RecetteFavorite::class.java)
            startActivity(intent)
        }

        // Utilise androidx.appcompat.widget.SearchView chat gpt
        val searchView: SearchView = view.findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    onSearchCallback?.invoke(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return view
    }

    fun setOnSearchCallback(callback: (String) -> Unit) {
        onSearchCallback = callback
    }
}
