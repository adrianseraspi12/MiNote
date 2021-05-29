package com.suzei.minote.data.repository

import com.suzei.minote.data.Result
import com.suzei.minote.data.local.entity.Todo
import com.suzei.minote.data.local.service.LocalService

class TodoDataSource(private val service: LocalService<Todo>) : DataSource<Todo> {

    companion object {
        fun instance(service: LocalService<Todo>): TodoDataSource = TodoDataSource(service)
    }

    override suspend fun getListOfData(): Result<List<Todo>> {
        return service.getListOfData()
    }

    override suspend fun getData(id: String): Result<Todo> {
        return service.getData(id)
    }

    override suspend fun save(data: Todo): Result<Todo> {
        return service.save(data)
    }

    override suspend fun update(data: Todo): Result<Nothing> {
        return service.update(data)
    }

    override suspend fun delete(data: Todo): Result<Nothing> {
        return service.delete(data)
    }

}