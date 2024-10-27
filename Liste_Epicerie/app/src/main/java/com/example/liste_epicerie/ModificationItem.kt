package com.example.liste_epicerie

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.room.Room
import com.example.liste_epicerie.data.Item
import com.example.liste_epicerie.data.ItemDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModificationItem: AppCompatActivity() {
    private lateinit var editTextName: EditText
    private lateinit var editTextQuantity: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var imageViewItem: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var db: ItemDatabase
    private var currentItem: Item? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modification_item)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialisation des vues
        editTextName = findViewById(R.id.editTextName)
        editTextQuantity = findViewById(R.id.editTextQuantity)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        imageViewItem = findViewById(R.id.imageViewItem)

        val buttonSelectImage: Button = findViewById(R.id.buttonSelectImage)
        val buttonSave: Button = findViewById(R.id.buttonSave)
        val buttonDelete: Button = findViewById(R.id.buttonDelete)

        // Initialisation de la base de données Room
        db = Room.databaseBuilder(
            applicationContext,
            ItemDatabase::class.java, "item_db"
        ).build()

        // Récupérer l'ID de l'item via Intent
        val itemId = intent.getIntExtra("ITEM_ID", -1)

        // Charger l'item en arrière-plan avec une coroutine
        CoroutineScope(Dispatchers.IO).launch {
            currentItem = db.itemDao().getItemById(itemId)

            // Mettre à jour l'interface utilisateur sur le thread principal
            withContext(Dispatchers.Main) {
                currentItem?.let {
                    editTextName.setText(it.name)
                    editTextQuantity.setText(it.quantity.toString())
                    // Charger la catégorie et l'image ici si nécessaire
                }
            }
        }

        // Bouton pour changer l'image
        buttonSelectImage.setOnClickListener {
            selectImage()
        }

        // Bouton pour enregistrer les modifications
        buttonSave.setOnClickListener {
            saveChanges()
        }

        // Bouton pour supprimer l'item
        buttonDelete.setOnClickListener {
            deleteItem()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                // Navigate back to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Méthode pour choisir une image depuis la galerie
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            imageViewItem.setImageURI(selectedImageUri)
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu_modification_item, menu)
        return super.onCreateOptionsMenu(menu)
    }


    // Enregistrer les modifications dans la base de données
    private fun saveChanges() {
        val newName = editTextName.text.toString()
        val newQuantity = editTextQuantity.text.toString().toInt()
        val newCategory = spinnerCategory.selectedItem.toString()

        currentItem?.let { item ->
            item.name = newName
            item.quantity = newQuantity
            item.category = newCategory
            item.imageUri = selectedImageUri?.toString()

            // Enregistrer les modifications en arrière-plan
            CoroutineScope(Dispatchers.IO).launch {
                db.itemDao().update(item)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ModificationItem, "Item modifié", Toast.LENGTH_SHORT).show()
                    finish() // Fermer l'activité
                }
            }
        }
    }

    // Supprimer l'item de la base de données
    private fun deleteItem() {
        currentItem?.let { item ->
            // Supprimer l'item en arrière-plan
            CoroutineScope(Dispatchers.IO).launch {
                db.itemDao().delete(item)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ModificationItem, "Item supprimé", Toast.LENGTH_SHORT).show()
                    finish() // Fermer l'activité
                }
            }
        }
    }
}