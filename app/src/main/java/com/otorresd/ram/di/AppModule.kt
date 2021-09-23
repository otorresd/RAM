package com.otorresd.ram.di

import android.app.Application
import androidx.room.Room
import com.otorresd.ram.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application.applicationContext,
            AppDatabase::class.java, dataBaseName)
            .enableMultiInstanceInvalidation()
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideKtorClient(): HttpClient {
        return HttpClient(OkHttp){
            engine {
                config {
                    followRedirects(false)
                }
            }
            install(JsonFeature) {
                serializer = GsonSerializer(){
                    serializeNulls()
                }
            }
        }
    }
}

val dataBaseName: String
    get() = "RAMDB"