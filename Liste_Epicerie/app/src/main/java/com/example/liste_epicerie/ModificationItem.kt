package com.example.liste_epicerie

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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

        // Initialisation de la base de données Room
        db = Room.databaseBuilder(
            applicationContext,
            ItemDatabase::class.java, "item_db"
        ).build()

        // Récupérer l'ID de l'item via Intent
        val itemId = intent.getIntExtra("ITEM_ID", -1)

        if (itemId != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                currentItem = db.itemDao().getItemById(itemId)
                withContext(Dispatchers.Main) {
                    currentItem?.let { item ->
                        editTextName.setText(item.name)
                        editTextQuantity.setText(item.quantity.toString())
                        spinnerCategory.setSelection(resources.getStringArray(R.array.category_array).indexOf(item.category))
                        val uri: Uri? = item.imageUri?.let { Uri.parse(it) }
                        if (item.imageUri != null) {
                            imageViewItem.setImageURI(uri)
                            selectedImageUri = uri
                        }

                    }
                }
            }
        }

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


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                // Navigate back to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_done -> {

                if (editTextName.text.isNotEmpty() && editTextQuantity.text.isNotEmpty() && spinnerCategory.selectedItem != null) {
                    saveChanges()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(this, "Veuillez remplir toutes les informations", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Méthode pour choisir une image depuis la galerie
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, 100)
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val file = File(filesDir, "selected_image.jpg")
            val outputStream = FileOutputStream(file)

            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val uri = data.data

            val imagePath = uri?.let { saveImageToInternalStorage(it) }
            selectedImageUri = Uri.parse(imagePath)
            if (imagePath != null) {
                imageViewItem.setImageURI(selectedImageUri)
            } else {
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu_modification_item, menu)
        return super.onCreateOptionsMenu(menu)
    }




}