package com.ema.cooknation.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocalRecipeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecipe(localRecipe: LocalRecipe)

    @Query("SELECT * FROM recipe_table ORDER BY date ASC")
    fun readAllData(): LiveData<List<LocalRecipe>>
}