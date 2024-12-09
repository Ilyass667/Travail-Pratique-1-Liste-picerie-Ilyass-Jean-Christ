package com.example.liste_epicerie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Recette : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recette)

        val searchFragment = supportFragmentManager.findFragmentById(R.id.fragmentSearch) as SearchFragment
        val recyclerFragment = supportFragmentManager.findFragmentById(R.id.fragmentRecycler) as RecyclerFragment

        // Connecter la recherche à l'affichage des recettes
        searchFragment.setOnSearchCallback { query ->
            recyclerFragment.updateRecipes(query) // Envoie la requête API pour récupérer les données
        }
    }
}
