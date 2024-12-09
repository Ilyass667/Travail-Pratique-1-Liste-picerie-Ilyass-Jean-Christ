package com.example.liste_epicerie

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.liste_epicerie.data.Item
import com.example.liste_epicerie.data.ItemDatabase
import com.example.liste_epicerie.data.PanierDatabase
import com.example.liste_epicerie.data.PanierItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PanierViewModel(application: Application) : AndroidViewModel(application) {

    private val db: ItemDatabase = Room.databaseBuilder(
        application.applicationContext,
        ItemDatabase::class.java, "item_db"
    ).build()

    private val panierDb: PanierDatabase = Room.databaseBuilder(
        application.applicationContext,
        PanierDatabase::class.java, "panier_db"
    ).build()

        suspend fun getAllPanierItems(): List<PanierItem> {
        return withContext(Dispatchers.IO) {
            panierDb.panierItemDao().getAllPanierItems()
        }
    }

    fun moveToCategories(panierItem: PanierItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = Item(
                id = panierItem.id,
                name = panierItem.name,
                quantity = panierItem.quantity,
                category = panierItem.category,
                imageUri = panierItem.imageUri
            )
            panierDb.panierItemDao().delete(panierItem)
            db.itemDao().insert(item)
        }
    }
}