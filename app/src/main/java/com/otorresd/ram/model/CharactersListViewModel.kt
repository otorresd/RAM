package com.otorresd.ram.model

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.otorresd.ram.paging.CharactersRemoteMediator
import com.otorresd.ram.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class CharactersListViewModel @Inject internal constructor(private val appDatabase: AppDatabase, val remoteMediator: CharactersRemoteMediator): ViewModel() {
    val charactersDao = appDatabase.characterDao()
    val pager = Pager(
        config = PagingConfig(pageSize = 20),
        remoteMediator = remoteMediator
    ) {
        charactersDao.pagingSource()
    }

    val charactersSize = appDatabase.characterDao().countOfCharacters()
}