package com.suzei.minote.data.repository

import com.suzei.minote.data.Result

interface DataSource<T> {
    suspend fun getListOfData(): Result<List<T>>
    suspend fun getData(id: String): Result<T>
    suspend fun save(data: T): Result<T>
    suspend fun update(data: T): Result<Nothing>
    suspend fun delete(data: T): Result<Nothing>
}