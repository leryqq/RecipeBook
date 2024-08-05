package com.example.recipebook.mainpages.shoplist.repository

import androidx.lifecycle.LiveData
import com.example.recipebook.mainpages.shoplist.ListViewModel
import com.example.recipebook.mainpages.shoplist.database.Dao
import com.example.recipebook.mainpages.shoplist.database.ShopListEntity

class ShopListItemRealization(private val dao: Dao): ShopItemListRepository {
    override val allItems: LiveData<List<ShopListEntity>>
        get() = dao.getAllShopListItems()

    override suspend fun insertItem(shopListEntity: ShopListEntity, onSuccess: () -> Unit) {
        dao.insert(shopListEntity)
        onSuccess()
    }

    override suspend fun updateItem(shopListEntity: ShopListEntity, onSuccess: () -> Unit) {
        dao.update(shopListEntity)
        onSuccess()
    }

    override suspend fun deleteItem(shopListEntity: ShopListEntity, onSuccess: () -> Unit) {
        dao.delete(shopListEntity)
        onSuccess()
    }


}