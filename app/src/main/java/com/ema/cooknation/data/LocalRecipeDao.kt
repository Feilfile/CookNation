package com.ema.cooknation.data

import androidx.lifecycle.LiveData
import androidx.room.*
import java.nio.channels.SelectableChannel

@Dao
interface LocalRecipeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecipe(localRecipe: LocalRecipe)

    @Delete
    suspend fun deleteRecipe(localRecipe: LocalRecipe)

    @Query("DELETE FROM recipe_table WHERE docId= :docId")
    fun deleteLocalRecipe(docId: String)

    @Query("SELECT * FROM recipe_table WHERE docId = :docId LIMIT 1")
    fun selectDocumentByDocID(docId: String): LiveData<LocalRecipe>

    @Query("SELECT * FROM recipe_table ORDER BY date ASC")
    fun readAllData(): LiveData<List<LocalRecipe>>
}