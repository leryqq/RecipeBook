package com.example.recipebook.mainpages.shoplist.bottomsheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipebook.REPOSITORY
import com.example.recipebook.mainpages.shoplist.database.ShopListEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShopItemViewModel: ViewModel() {

    //выполняем функции на второстепенном потоке coroutine

    fun insert(listViewModel: ShopListEntity, onSuccess:() -> Unit) =
        viewModelScope.launch (Dispatchers.IO){
        REPOSITORY.insertItem(listViewModel){
            onSuccess()
        }
    }

    fun update(listViewModel: ShopListEntity, onSuccess: () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            REPOSITORY.updateItem(listViewModel) {
                onSuccess()
            }
        }

    fun delete(listViewModel: ShopListEntity, onSuccess: () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) {
            REPOSITORY.deleteItem(listViewModel) {
                onSuccess()
            }
        }
}