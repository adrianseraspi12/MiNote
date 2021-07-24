package com.suzei.minote.data.local.service

import com.suzei.minote.data.Result
import com.suzei.minote.data.local.entity.Notes

interface LocalService<T> {
    suspend fun save(data: T): Result<T>
    suspend fun update(data: T): Result<Nothing>
    suspend fun getData(itemId: String): Result<T>
    suspend fun getListOfData(): Result<List<T>>
    suspend fun delete(data: T): Result<Nothing>
}