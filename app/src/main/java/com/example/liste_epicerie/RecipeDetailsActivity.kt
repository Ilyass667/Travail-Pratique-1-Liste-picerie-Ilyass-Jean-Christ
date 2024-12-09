package com.example.liste_epicerie

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

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

        // Remplir les vues avec les données reçues
        titleTextView.text = recipeTitle
        categoryTextView.text = recipeCategory
        instructionsTextView.text = recipeInstructions
        Glide.with(this).load(recipeImage).into(imageView)

        // Gérer le clic sur le bouton retour
        backButton.setOnClickListener {
            finish() // Terminer l'activité pour revenir à l'écran précédent
        }
    }
}
