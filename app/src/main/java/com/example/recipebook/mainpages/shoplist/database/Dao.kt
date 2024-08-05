package com.example.recipebook.mainpages.shoplist.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(shopListEntity: ShopListEntity) //Запись данных в БД

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(shopListEntity: ShopListEntity) //обновление данных в БД

    @Delete
    suspend fun delete(shopListEntity: ShopListEntity) //удаление

    @Query("SELECT * FROM ShopList_Items")
    fun getAllShopListItems(): LiveData<List<ShopListEntity>> //Получение ВСЕХ данных из БД
}