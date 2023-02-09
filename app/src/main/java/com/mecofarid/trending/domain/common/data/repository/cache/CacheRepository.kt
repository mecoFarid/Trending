package com.mecofarid.trending.domain.common.data.repository.cache

import com.mecofarid.trending.domain.common.data.datasource.Datasource
import com.mecofarid.trending.domain.common.data.repository.Repository
import com.mecofarid.trending.domain.common.either.Either
import com.mecofarid.trending.domain.common.data.Operation
import com.mecofarid.trending.domain.common.data.Query

class CacheRepository<T, E>(
    private val cacheDatasource: Datasource<T, E>,
    private val mainDatasource: Datasource<T, E>
) : Repository<T, E> {

    override suspend fun get(query: Query, operation: Operation): Either<E, T> {
        return when (operation) {
            Operation.SyncMainOperation -> getSyncedData(query)
            Operation.CacheElseSyncMainOperation -> getCachedElseSyncedData(query)
        }
    }

    private suspend fun getSyncedData(query: Query): Either<E, T> =
        mainDatasource.get(query)
            .onRight {
                cacheDatasource.put(query, it)
                return getCachedData(query)
            }
            .onLeft {
                return getCachedData(query)
            }


    private suspend fun getCachedElseSyncedData(query: Query): Either<E, T> =
        getCachedData(query)
            .onLeft {
                return get(query, Operation.SyncMainOperation)
            }

    private suspend fun getCachedData(query: Query): Either<E, T> {
        return cacheDatasource.get(query)
    }
}
