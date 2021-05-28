package com.suzei.minote.data.repository

import com.suzei.minote.data.Result
import com.suzei.minote.data.local.entity.Notes
import com.suzei.minote.data.local.service.LocalService

class NoteDataSource(private val service: LocalService<Notes>) : DataSource<Notes> {

    companion object {
        fun instance(service: LocalService<Notes>): NoteDataSource = NoteDataSource(service)
    }

    override suspend fun getListOfData(): Result<List<Notes>> {
        return service.getListOfData()
    }

    override suspend fun save(data: Notes): Result<Nothing> {
        return service.save(data)
    }

    override suspend fun update(data: Notes): Result<Nothing> {
        return service.update(data)
    }

    override suspend fun delete(data: Notes): Result<Nothing> {
        return service.delete(data)
    }
}