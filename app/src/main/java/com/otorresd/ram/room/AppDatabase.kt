package com.otorresd.ram.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.otorresd.ram.room.dao.CharactersDao
import com.otorresd.ram.room.entities.CharacterE

@Database(entities = [CharacterE::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun characterDao(): CharactersDao
}