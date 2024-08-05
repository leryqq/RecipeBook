package com.example.recipebook.mainpages.shoplist.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [ShopListEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = ShopListDB.Migration1to2::class)
    ])
abstract class ShopListDB : RoomDatabase() {
    abstract fun getDao(): Dao

    @DeleteColumn(tableName = "ShopList_Items", columnName = "Quantity")
    class Migration1to2: AutoMigrationSpec

    //companion object для получения доступа
    companion object{
        private var database: ShopListDB ?= null
        fun getDB(context: Context): ShopListDB {
            return Room.databaseBuilder(
                context.applicationContext,
                ShopListDB::class.java,
                "shop_list.db"
            ).build()
        }

        //проверка создана бд или нет. Если нет, то создаем, если да, то обращаемся к созданной
        @Synchronized
        fun getInstanceDB(context: Context): ShopListDB{
            return if (database == null){
                database = Room.databaseBuilder(context, ShopListDB::class.java, "shop_list_db")
                    .addMigrations()
                    .build()
                database as ShopListDB
            }
            else{
                database as ShopListDB
            }
        }
    }
}