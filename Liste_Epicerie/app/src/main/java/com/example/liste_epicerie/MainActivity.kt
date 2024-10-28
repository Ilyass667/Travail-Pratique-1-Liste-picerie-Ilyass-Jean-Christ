package com.example.liste_epicerie

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.Menu
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.liste_epicerie.data.Category
import com.example.liste_epicerie.data.ItemDatabase
import com.example.liste_epicerie.data.Item
import com.example.liste_epicerie.data.Panier
import com.example.liste_epicerie.data.PanierDatabase
import com.example.liste_epicerie.data.PanierItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GenericItem(var name: String, var quantity: Int, var image: Int)

class GenericItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val layout: ConstraintLayout
    val nomProduit: TextView
    val img: ImageView
    val img2: ImageView

    init {
        layout = itemView as ConstraintLayout
        nomProduit = itemView.findViewById(R.id.nomProduits)
        img = itemView.findViewById(R.id.barre)
        img2 = itemView.findViewById(R.id.infos)
    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var db: ItemDatabase
    private lateinit var panierDb: PanierDatabase
    private val panierList = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            ItemDatabase::class.java, "item_db"
        ).build()

        panierDb = Room.databaseBuilder(
            applicationContext,
            PanierDatabase::class.java, "panier_db"
        ).build()

        // Afficher le nom de l'application dans le menu
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialisation du bouton
        val button3: Button = findViewById(R.id.button3)

        // Configuration de RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val recyclerViewPanier : RecyclerView = findViewById(R.id.recyclerViewPanier)
        findViewById<TextView>(R.id.textPanier).setText(R.string.panier)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerViewPanier.layoutManager = LinearLayoutManager(this)

        // Configuration de RecyclerView Panier

        // Charger les éléments depuis la base de données
        CoroutineScope(Dispatchers.IO).launch {
            val itemList = db.itemDao().getAllItems()
            val panierItemList = panierDb.panierItemDao().getAllPanierItems()
            Log.d(TAG, "onCreate: $itemList")
            Log.d(TAG, "onCreate: $panierItemList")

            withContext(Dispatchers.Main) {
                val categories = itemList.groupBy { it.category }.map { Category(it.key, it.value.toMutableList()) }.toMutableList()
                recyclerView.adapter = CategoryAdapter(categories, { item ->
                    moveToPanier(item)
                })
                recyclerViewPanier.adapter = PanierItemAdapter(panierItemList) { panierItem ->
                    moveToCategories(panierItem)
                }
                panierList.addAll(panierItemList.map { Item(it.id, it.name, it.quantity, it.category, it.imageUri) })
            }
        }

        // Ajout de l'action lors du clic sur le bouton
        button3.setOnClickListener {
            // Création d'un intent pour passer à la nouvelle activité
            val intent = Intent(this, ModificationItem::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun moveToPanier(item: Item) {
        CoroutineScope(Dispatchers.IO).launch {
            // Ajouter l'élément à la base de données du panier
            val panierItem = PanierItem(
                name = item.name,
                quantity = item.quantity,
                category = item.category,
                imageUri = item.imageUri
            )

            db.itemDao().delete(item)
            panierDb.panierItemDao().insert(panierItem)

            withContext(Dispatchers.Main) {
                // Supprimer l'élément de son RecyclerView actuel
                val currentAdapter = findViewById<RecyclerView>(R.id.recyclerView).adapter as CategoryAdapter
                val panierAdapter = findViewById<RecyclerView>(R.id.recyclerViewPanier).adapter as PanierItemAdapter
                val category = currentAdapter.categories.find { it.items.contains(item) }
                category?.items?.remove(item)
                currentAdapter.notifyDataSetChanged()
                currentAdapter.checkAndRemoveEmptyCategories()

                // Ajouter l'élément au RecyclerView du panier
                panierList.add(item)
                panierAdapter.items.add(panierItem)
                panierAdapter.notifyDataSetChanged()
                findViewById<TextView>(R.id.textPanier).visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun moveToCategories(panierItem: PanierItem) {
        CoroutineScope(Dispatchers.IO).launch {
            // Ajouter l'élément à nouveau à la base de données principale
            val item = Item(
                id = panierItem.id,
                name = panierItem.name,
                quantity = panierItem.quantity,
                category = panierItem.category,
                imageUri = panierItem.imageUri
            )
            panierDb.panierItemDao().delete(panierItem)
            db.itemDao().insert(item)

            // Supprimer l'élément de la base de données du panier

            withContext(Dispatchers.Main) {
                // Supprimer l'élément du RecyclerView du panier
                deleteFromRecycleView(panierItem)
                // Ajouter l'élément au RecyclerView des catégories
                val currentAdapter = findViewById<RecyclerView>(R.id.recyclerView).adapter as CategoryAdapter
                val category = currentAdapter.categories.find { it.name == item.category }
                if (category != null) {
                    category.items.add(item)
                } else {
                    currentAdapter.categories.add(Category(item.category, mutableListOf(item)))
                }
                currentAdapter.notifyDataSetChanged()
            }
        }
    }

    fun deleteFromRecycleView(item: PanierItem) {
        val panierAdapter = findViewById<RecyclerView>(R.id.recyclerViewPanier).adapter as PanierItemAdapter
        panierAdapter.items.remove(item)
        panierAdapter.notifyDataSetChanged()
        if (panierAdapter.items.isEmpty()) {
            findViewById<TextView>(R.id.textPanier).visibility = View.GONE
        }
    }
}
