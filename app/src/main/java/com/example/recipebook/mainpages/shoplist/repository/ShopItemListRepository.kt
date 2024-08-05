package com.example.recipebook.mainpages.shoplist.repository

import androidx.lifecycle.LiveData
import com.example.recipebook.mainpages.shoplist.ListViewModel
import com.example.recipebook.mainpages.shoplist.database.ShopListEntity

//набор функций для работы с БД

interface ShopItemListRepository {
    val allItems: LiveData<List<ShopListEntity>>
    suspend fun insertItem(shopListEntity: ShopListEntity, onSuccess:() -> Unit) // добавление записи
    suspend fun updateItem(shopListEntity: ShopListEntity, onSuccess:() -> Unit) // обновление записи
    suspend fun deleteItem(shopListEntity: ShopListEntity, onSuccess:() -> Unit) // удаление записи
}