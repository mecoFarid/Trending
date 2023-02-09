package com.mecofarid.trending.domain.common.data.datasource.network

import com.mecofarid.trending.domain.common.data.Query

interface NetworkService<T> {
    suspend fun get(query: Query): T
    suspend fun put(query: Query, data: T): T
}
