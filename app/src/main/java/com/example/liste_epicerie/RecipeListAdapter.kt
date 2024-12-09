import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.liste_epicerie.R
import com.example.liste_epicerie.Recipe

class RecipeListAdapter(
    private val onRecipeClick: (Recipe) -> Unit // lambda pour les clics
) : RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder>() {

    private var recipes = listOf<Recipe>()

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.recipe_title)
        val description: TextView = view.findViewById(R.id.recipe_description)
        val image: ImageView = view.findViewById(R.id.recipe_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.title.text = recipe.strMeal
        holder.description.text = recipe.strCategory
        Glide.with(holder.itemView.context)
            .load(recipe.strMealThumb)
            .into(holder.image)

        // Gestion des clics
        holder.itemView.setOnClickListener {
            onRecipeClick(recipe) // Appelle la lambda avec la recette
        }
    }

    override fun getItemCount(): Int = recipes.size

    fun submitList(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}

