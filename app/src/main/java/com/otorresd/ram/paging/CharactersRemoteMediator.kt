package com.otorresd.ram.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import coil.network.HttpException
import com.otorresd.ram.mapping.Paging
import com.otorresd.ram.room.AppDatabase
import com.otorresd.ram.room.entities.CharacterE
import io.ktor.client.*
import io.ktor.client.request.*
import java.io.IOException

@ExperimentalPagingApi
class CharactersRemoteMediator(private val appDatabase: AppDatabase, private val ktorClient: HttpClient): RemoteMediator<Int, CharacterE>() {
    val characterDao = appDatabase.characterDao()
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterE>
    ): MediatorResult {
        return try {
            // The network load method takes an optional after=<user.id>
            // parameter. For every page after the first, pass the last user
            // ID to let it continue from where it left off. For REFRESH,
            // pass null to load the first page.
            val loadKey = when (loadType) {
                LoadType.REFRESH -> "https://rickandmortyapi.com/api/character"
                // In this example, you never need to prepend, since REFRESH
                // will always load the first page in the list. Immediately
                // return, reporting end of pagination.
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    Log.w("Append","${state.lastItemOrNull()}")
                    val lastItem = state.lastItemOrNull()
                        ?: characterDao.getLastCharacterOrNull() ?: return MediatorResult.Success(
                            endOfPaginationReached = true
                        )

                    // You must explicitly check if the last item is null when
                    // appending, since passing null to networkService is only
                    // valid for initial load. If lastItem is null it means no
                    // items were loaded after the initial REFRESH and there are
                    // no more items to load.

                    lastItem.nextPage
                }
            }

            // Suspending network load via Retrofit. This doesn't need to be
            // wrapped in a withContext(Dispatcher.IO) { ... } block since
            // Retrofit's Coroutine CallAdapter dispatches on a worker
            // thread.
            val response: Paging = ktorClient.get(loadKey ?: "https://rickandmortyapi.com/api/character")

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    characterDao.clearAll()
                }

                // Insert new users into database, which invalidates the
                // current PagingData, allowing Paging to present the updates
                // in the DB.
                characterDao.insertAll(response.toCharacterEList())
            }

            MediatorResult.Success(
                endOfPaginationReached = response.info.next == null
            )
        } catch (e: IOException) {
            Log.e("PagingError", e.toString())
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e("PagingError", e.toString())
            MediatorResult.Error(e)
        }
    }
}