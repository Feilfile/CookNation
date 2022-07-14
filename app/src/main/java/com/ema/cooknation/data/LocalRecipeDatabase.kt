package com.ema.cooknation.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//no version logging yet
@Database(entities = [LocalRecipe::class], version = 1, exportSchema = false)
abstract class LocalRecipeDatabase: RoomDatabase() {

    abstract fun localRecipeDao(): LocalRecipeDao

    //Singleton creation
    companion object {
        @Volatile
        private var  INSTANCE: LocalRecipeDatabase? = null

        fun getDatabase(context: Context): LocalRecipeDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalRecipeDatabase::class.java,
                    "recipe_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}