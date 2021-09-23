package com.otorresd.ram.mapping

import com.otorresd.ram.room.entities.CharacterE

data class Paging(val info: Info, val results: List<Character>) {

    fun toCharacterEList(): List<CharacterE>{
        return results.map { it.toCharacterE().apply { nextPage = info.next } }
    }
}

data class Character(val id: Int, val name: String, val image: String){

    fun toCharacterE(): CharacterE{
        return CharacterE(id = id, name = name, image = image)
    }
}

data class Info(val next: String?)