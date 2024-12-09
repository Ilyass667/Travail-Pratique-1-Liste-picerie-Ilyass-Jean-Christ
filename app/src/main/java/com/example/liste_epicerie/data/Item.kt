package com.example.liste_epicerie.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update

// Définition de l'entité Item
@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var quantity: Int,
    var category: String,
    var imageUri: String?
)

// Définition de l'entité PanierItem
@Entity
data class PanierItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var quantity: Int,
    var category: String,
    var imageUri: String?
)

// Base de données pour les éléments du panier
@Database(entities = [PanierItem::class], version = 1)
abstract class PanierDatabase : RoomDatabase() {
    abstract fun panierItemDao(): PanierItemDao
}

// DAO pour les éléments du panier
@Dao
interface PanierItemDao {
    @Insert
    fun insert(panierItem: PanierItem)

    @Query("SELECT * FROM PanierItem")
    fun getAllPanierItems(): MutableList<PanierItem>

    @Delete
    fun delete(item: PanierItem)

    @Query("DELETE FROM PanierItem")
    fun deleteAll()
}


// Définition de la classe Category
data class Category(val name: String, val items: MutableList<Item>)

// Définition de la classe Panier
data class Panier(val items: MutableList<PanierItem>)


// DAO pour les éléments
@Dao
interface ItemDao {
    @Insert
    fun insert(item: Item)

    @Update
    fun update(item: Item)

    @Delete
    fun delete(item: Item)

    @Query("SELECT * FROM Item WHERE id = :id")
    fun getItemById(id: Int): Item?

    @Query("SELECT * FROM Item")
    fun getAllItems(): MutableList<Item>
}

// Base de données pour les éléments
@Database(entities = [Item::class], version = 1)
abstract class ItemDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}