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

class ListeAchatViewModel(application: Application) : AndroidViewModel(application) {



    private val db: ItemDatabase = Room.databaseBuilder(
        application.applicationContext,
        ItemDatabase::class.java, "item_db"
    ).build()

    private val panierDb: PanierDatabase = Room.databaseBuilder(
        application.applicationContext,
        PanierDatabase::class.java, "panier_db"
    ).build()

        suspend fun getAllItems(): List<Item> {
        return withContext(Dispatchers.IO) {
            db.itemDao().getAllItems()
        }
    }

    fun moveToPanier(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            val panierItem = PanierItem(
                name = item.name,
                quantity = item.quantity,
                category = item.category,
                imageUri = item.imageUri
            )
//            db.itemDao().delete(item)
            panierDb.panierItemDao().insert(panierItem)
        }
    }


}


