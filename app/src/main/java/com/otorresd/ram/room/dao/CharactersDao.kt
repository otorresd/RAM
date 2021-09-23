package com.otorresd.ram.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.otorresd.ram.room.entities.CharacterE

@Dao
interface CharactersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<CharacterE>)

    @Query("SELECT * FROM characters ORDER BY id")
    fun pagingSource(): PagingSource<Int, CharacterE>

    @Query("SELECT * FROM characters WHERE id = (SELECT MAX(id) FROM characters)")
    suspend fun getLastCharacterOrNull(): CharacterE?

    @Query("DELETE FROM characters")
    suspend fun clearAll()
}