package com.otorresd.ram.model

import androidx.lifecycle.ViewModel
import com.otorresd.ram.room.AppDatabase
import com.otorresd.ram.room.entities.CharacterE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject internal constructor(private val appDatabase: AppDatabase): ViewModel() {

    fun getCharacterById(id: String): Flow<CharacterE> {
        return id.let { appDatabase.characterDao().getCharacterById(it) }
    }
}