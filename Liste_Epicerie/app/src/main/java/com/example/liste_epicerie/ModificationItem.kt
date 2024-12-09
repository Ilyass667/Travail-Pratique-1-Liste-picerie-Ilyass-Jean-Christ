package com.example.liste_epicerie

import android.content.Intent
import android.graphics.Bitmap
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
        val buttonPrendrePhoto = findViewById<Button>(R.id.buttonprisePhoto)
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

        buttonPrendrePhoto.setOnClickListener {
            prendrePhoto()
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                // Navigation à MainActivity
                finish()
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
    private fun prendrePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 101)
    }



override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if ((requestCode == 100) && resultCode == RESULT_OK && data != null) {
        val uri = data.data

        val imagePath = uri?.let { saveImageToInternalStorage(it) }
        selectedImageUri = Uri.parse(imagePath)
        if (imagePath != null) {
            imageViewItem.setImageURI(selectedImageUri)
        } else {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    } else if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
        val bitmap = data.extras?.get("data") as? Bitmap
        bitmap?.let {
            val uri = saveBitmapToInternalStorage(it)
            selectedImageUri = Uri.parse(uri)
            if (uri != null) {
                imageViewItem.setImageURI(selectedImageUri)
            } else {
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

    // source: https://www.youtube.com/watch?v=iumKvmpOgOA
    //source 2: https://medium.com/@ankitashetty/android-code-to-save-image-in-internal-storage-on-button-click-891f1de39915
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

    private fun saveBitmapToInternalStorage(bitmap: Bitmap): String? {
        return try {
            val file = File(filesDir, "captured_image.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
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
    val newImageUri = selectedImageUri?.toString()

    CoroutineScope(Dispatchers.IO).launch {
        if (currentItem == null) {
            // Créé un nouveaux item
            val newItem = Item(name = newName, quantity = newQuantity, category = newCategory, imageUri = newImageUri)
            db.itemDao().insert(newItem)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ModificationItem, "Item créé", Toast.LENGTH_SHORT).show()
                finish() // fermer l'activité
            }
            Log.d("ModificationItem", "New item inserted: $newItem")
        } else {
            // Update l'item existant
            currentItem?.let { item ->
                item.name = newName
                item.quantity = newQuantity
                item.category = newCategory
                item.imageUri = newImageUri

                db.itemDao().update(item)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ModificationItem, "Item modifié", Toast.LENGTH_SHORT).show()
                    finish() //fermer l'activité
                }
            }
        }
    }
}


}