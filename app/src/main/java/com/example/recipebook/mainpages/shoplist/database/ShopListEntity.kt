package com.example.recipebook.mainpages.shoplist.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "ShopList_Items")
data class ShopListEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    @ColumnInfo(name = "Name")
    var name: String,

    @ColumnInfo(name = "Checked")
    var checked: Boolean?
) : Serializable
