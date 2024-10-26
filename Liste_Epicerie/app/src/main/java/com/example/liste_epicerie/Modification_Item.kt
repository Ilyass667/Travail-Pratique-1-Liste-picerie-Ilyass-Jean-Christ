package com.example.liste_epicerie

import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class Modification_Item : AppCompatActivity() {
    private var quantity: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modification_item)


        //Afficher le nom de l'application dans le menu
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val editText = findViewById<EditText>(R.id.editTextText)
        val buttonMoins = findViewById<Button>(R.id.button)
        val buttonPlus = findViewById<Button>(R.id.button2)

        buttonMoins.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateEditText(editText)
            }
        }

        buttonPlus.setOnClickListener {
            quantity++
            updateEditText(editText)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu_modification_item, menu)
        return super.onCreateOptionsMenu(menu)
    }


    private fun updateEditText(editText: EditText) {
        val currentText = editText.text.toString()
        val baseText = currentText.replace(Regex("\\(\\d+\\)\$"), "")
        editText.setText("$baseText($quantity)")
        editText.setSelection(baseText.length)
    }
}
