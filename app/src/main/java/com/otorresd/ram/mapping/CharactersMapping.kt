package com.otorresd.ram.mapping

import com.otorresd.ram.room.entities.CharacterE

data class Paging(val info: Info, val results: List<Character>) {

    fun toCharacterEList(): List<CharacterE>{
        return results.map { it.toCharacterE().apply { nextPage = info.next } }
    }
}

data class Character(val id: Int,
                     val name: String,
                     val image: String,
                     val status: String,
                     val species: String,
                     val type: String,
                     val gender: String,
                     val origin: Location,
                     val location: Location){

    fun toCharacterE(): CharacterE{
        return CharacterE(id = id,
            name = name,
            image = image,
            status = status,
            species = species,
            type = type,
            gender = gender,
            origin = origin,
            location = location)
    }
}

data class Location(val name: String, val url: String)

data class Info(val next: String?)