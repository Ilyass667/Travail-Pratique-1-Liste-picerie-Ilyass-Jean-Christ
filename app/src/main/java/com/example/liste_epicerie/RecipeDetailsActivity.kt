package com.example.liste_epicerie

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RecipeDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        // Récupérer les données passées par l'intent
        val recipeTitle = intent.getStringExtra("recipeTitle")
        val recipeCategory = intent.getStringExtra("recipeCategory")
        val recipeInstructions = intent.getStringExtra("recipeInstructions")
        val recipeImage = intent.getStringExtra("recipeImage")

        // Référencer les vues
        val titleTextView: TextView = findViewById(R.id.recipe_detail_title)
        val categoryTextView: TextView = findViewById(R.id.recipe_detail_category)
        val instructionsTextView: TextView = findViewById(R.id.recipe_detail_instructions)
        val imageView: ImageView = findViewById(R.id.recipe_detail_image)
        val backButton: ImageButton = findViewById(R.id.back_button)
        val favoriteButton: Button = findViewById(R.id.favorite_button)

        // Remplir les vues avec les données reçues
        titleTextView.text = recipeTitle
        categoryTextView.text = recipeCategory
        instructionsTextView.text = recipeInstructions
        Glide.with(this).load(recipeImage).into(imageView)

        // Gérer le clic sur le bouton retour
        backButton.setOnClickListener {
            finish() // Terminer l'activité pour revenir à l'écran précédent
        }

        // Ajouter la recette aux favoris dans Firebase
        favoriteButton.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val favoritesRef = FirebaseDatabase.getInstance().reference
                    .child("Favorites")
                    .child(userId)

                val recipeId = recipeTitle?.replace(" ", "_") ?: "recipe" // Remplacer les espaces par _
                val recipeDetails = mapOf(
                    "title" to recipeTitle,
                    "category" to recipeCategory,
                    "instructions" to recipeInstructions,
                    "image" to recipeImage
                )

                favoritesRef.child(recipeId).setValue(recipeDetails)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Recette ajoutée aux favoris", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
