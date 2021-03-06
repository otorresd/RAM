package com.otorresd.ram.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.otorresd.ram.mapping.Location

@Entity(tableName = "characters")
data class CharacterE(@PrimaryKey val id: Int,
                      val name: String,
                      val image: String,
                      val status: String,
                      val species: String,
                      val type: String,
                      val gender: String,
                      var nextPage: String? = null,
                      @Embedded(prefix = "ori_") var origin: Location,
                      @Embedded(prefix = "loc_") var location: Location)