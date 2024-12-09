package com.example.liste_epicerie

import RecipeListAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecyclerFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recycler, container, false)

        // Configure RecyclerView
        recyclerView = view.findViewById(R.id.recipe_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecipeListAdapter { recipe ->
            // Navigation vers RecipeDetailsActivity
            //De chat gpt pour les instruction INTENT
            val intent = Intent(requireContext(), RecipeDetailsActivity::class.java).apply {
                putExtra("recipeTitle", recipe.strMeal)
                putExtra("recipeCategory", recipe.strCategory)
                putExtra("recipeInstructions", recipe.strInstructions)
                putExtra("recipeImage", recipe.strMealThumb)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        return view
    }



    // Fonction pour mettre à jour les recettes (appelée depuis SearchFragment)
    fun updateRecipes(query: String) {
        val progressBar = view?.findViewById<ProgressBar>(R.id.progress_bar)
        val emptyView = view?.findViewById<TextView>(R.id.empty_view)

        progressBar?.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.searchRecipes(query)
                if (response.isSuccessful) {
                    val meals = response.body()?.meals
                    CoroutineScope(Dispatchers.Main).launch {
                        if (meals.isNullOrEmpty()) {
                            // Aucun résultat trouvé
                            emptyView?.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            // Mettre à jour avec les résultats
                            val recipes = meals.map { meal ->
                                Recipe(
                                    idMeal = meal.idMeal,
                                    strMeal = meal.strMeal,
                                    strCategory = meal.strCategory,
                                    strInstructions = meal.strInstructions,
                                    strMealThumb = meal.strMealThumb
                                )
                            }
                            adapter.submitList(recipes)

                            emptyView?.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                        progressBar?.visibility = View.GONE
                    }
                } else {
                    // Erreur de réponse
                    CoroutineScope(Dispatchers.Main).launch {
                        showError("Erreur : ${response.code()} - ${response.message()}")
                        emptyView?.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        progressBar?.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                // Exception réseau ou autre
                CoroutineScope(Dispatchers.Main).launch {
                    showError("Une erreur est survenue : ${e.localizedMessage}")
                    emptyView?.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    progressBar?.visibility = View.GONE
                }
            }
        }
    }


    // Méthode pour afficher les erreurs pour nos TEST
    private fun showError(message: String) {
        println("Erreur : $message")
    }


}
