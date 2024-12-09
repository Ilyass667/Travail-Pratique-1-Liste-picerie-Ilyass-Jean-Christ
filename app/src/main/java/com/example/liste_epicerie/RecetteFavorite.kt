package com.example.liste_epicerie

import RecipeListAdapter
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RecetteFavorite : AppCompatActivity() {

    private lateinit var adapter: RecipeListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recette_favorite)

        // Initialiser RecyclerView
        recyclerView = findViewById(R.id.favorites_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecipeListAdapter { recipe ->
            // func lambda
            // Pour traces
            println("Recette cliquée : ${recipe.strMeal}")
        }
        recyclerView.adapter = adapter

        // Charger les recettes favorites
        loadFavorites()

        // Bouton Retour
        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish() // Terminer l'activité pour revenir à l'écran précédent
        }
    }

    private fun loadFavorites() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val favoritesRef = FirebaseDatabase.getInstance().reference
                .child("Favorites")
                .child(userId)

            favoritesRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val favoriteRecipes = snapshot.children.mapNotNull { recipeSnapshot ->
                        // Convertir manuellement chaque recette (aide de chatgpt)
                        val title = recipeSnapshot.child("title").value as? String
                        val category = recipeSnapshot.child("category").value as? String
                        val image = recipeSnapshot.child("image").value as? String
                        val instructions = recipeSnapshot.child("instructions").value as? String

                        if (title != null && category != null && image != null && instructions != null) {
                            Recipe(
                                idMeal = recipeSnapshot.key ?: "",
                                strMeal = title,
                                strCategory = category,
                                strInstructions = instructions,
                                strMealThumb = image
                            )
                        } else {
                            null // Ignorer les données invalides
                        }
                    }
                    adapter.submitList(favoriteRecipes)
                }
            }.addOnFailureListener { e ->
                println("Erreur lors de la lecture des favoris : ${e.message}")
            }
        }
    }

}
