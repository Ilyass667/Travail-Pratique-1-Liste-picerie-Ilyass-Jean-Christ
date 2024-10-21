package com.example.liste_epicerie

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}