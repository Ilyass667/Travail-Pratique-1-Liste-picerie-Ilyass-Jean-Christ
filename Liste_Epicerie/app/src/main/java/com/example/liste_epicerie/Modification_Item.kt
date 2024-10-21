package com.example.liste_epicerie

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class Modification_Item : AppCompatActivity() {
    private var quantity: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modification_item)


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


    private fun updateEditText(editText: EditText) {
        val currentText = editText.text.toString()
        val baseText = currentText.replace(Regex("\\(\\d+\\)\$"), "")
        editText.setText("$baseText($quantity)")
        editText.setSelection(baseText.length)
    }
}
