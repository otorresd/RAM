package com.otorresd.ram.di

import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import com.otorresd.ram.paging.CharactersRemoteMediator
import com.otorresd.ram.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.ktor.client.*

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @ExperimentalPagingApi
    @Provides
    fun provideDBRepository(db: AppDatabase, ktorClient: HttpClient): CharactersRemoteMediator {
        return CharactersRemoteMediator(appDatabase = db, ktorClient = ktorClient)
    }
}