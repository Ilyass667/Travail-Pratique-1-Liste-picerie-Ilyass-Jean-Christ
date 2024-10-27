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

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var quantity: Int,
    var category: String,
    var imageUri: String?
)

@Entity
data class PanierItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var quantity: Int,
    var category: String,
    var imageUri: String?
)
@Database(entities = [PanierItem::class], version = 1)
abstract class PanierDatabase : RoomDatabase() {
    abstract fun panierItemDao(): PanierItemDao
}

@Dao
interface PanierItemDao {
    @Insert
    fun insert(panierItem: PanierItem)

    @Query("SELECT * FROM PanierItem")
    fun getAllPanierItems(): MutableList<PanierItem>


    @Query ("DELETE FROM PanierItem")
    fun deleteAll()
}



data class Category(val name: String, val items: MutableList<Item>)
data class Panier(val items: MutableList<PanierItem>)


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

@Database(entities = [Item::class], version = 1)
abstract class ItemDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}