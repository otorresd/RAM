package com.otorresd.ram.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterE(@PrimaryKey val id: Int, val name: String, val image: String, var nextPage: String? = null)