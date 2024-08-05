package com.example.recipebook.mainpages.shoplist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.recipebook.REPOSITORY
import com.example.recipebook.mainpages.shoplist.database.ShopListDB
import com.example.recipebook.mainpages.shoplist.database.ShopListEntity
import com.example.recipebook.mainpages.shoplist.repository.ShopListItemRealization

class ListViewModel(application: Application): AndroidViewModel(application) {

    val context = application

    fun initDatabase(){
        val daoShopList = ShopListDB.getInstanceDB(context).getDao()
        REPOSITORY = ShopListItemRealization(daoShopList)
    }

    fun getAllItems(): LiveData<List<ShopListEntity>>{
        return REPOSITORY.allItems
    }
}