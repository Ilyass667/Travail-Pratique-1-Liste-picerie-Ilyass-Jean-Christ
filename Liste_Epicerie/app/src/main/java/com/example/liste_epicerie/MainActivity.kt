package com.example.liste_epicerie

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import android.net.Uri
import android.provider.MediaStore
import android.view.Menu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.liste_epicerie.data.Category
import com.example.liste_epicerie.data.ItemDatabase
import com.example.liste_epicerie.data.Item
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            ItemDatabase::class.java, "item-database"
        ).build()

        //Afficher le nom de l'application dans le menu
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialisation du bouton
        val button3: Button = findViewById(R.id.button3)

        // Set up RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load items from the database
    CoroutineScope(Dispatchers.IO).launch {
        var itemList = db.itemDao().getAllItems()

        if (itemList.isEmpty()) {
            // Add a dummy item for testing
            val dummyItem = Item(name = "Dummy Item", quantity = 1, category = "Test", imageUri = null)
            db.itemDao().insert(dummyItem)
            db.itemDao().insert(Item(name = "Dummy Item", quantity = 1, category = "Test", imageUri = null))
            db.itemDao().insert(Item(name = "Dummy Item", quantity = 1, category = "Test2", imageUri = null))
            db.itemDao().insert(Item(name = "Dummy Item", quantity = 1, category = "Test2", imageUri = null))
            db.itemDao().insert(Item(name = "Dummy Item", quantity = 1, category = "Test3", imageUri = null))
            db.itemDao().insert(Item(name = "Dummy Item", quantity = 1, category = "Test3", imageUri = null))
            itemList = db.itemDao().getAllItems() // Reload the items
        }


        withContext(Dispatchers.Main) {
            val categories = itemList.groupBy { it.category }.map { Category(it.key, it.value) }
            recyclerView.adapter = CategoryAdapter(categories)
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
