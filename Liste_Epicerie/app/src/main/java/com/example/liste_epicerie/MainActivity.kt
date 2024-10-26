package com.example.liste_epicerie

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        //Afficher le nom de l'application dans le menu
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialisation du bouton
        val button3: Button = findViewById(R.id.button3)

        // Ajout de l'action lors du clic sur le bouton
        button3.setOnClickListener {
            // Création d'un intent pour passer à la nouvelle activité
            val intent = Intent(this, Modification_Item::class.java)
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
