package com.suzei.minote.data.repository

interface Repository<T> {

    interface Listener<T> {

        fun onDataAvailable(data: T)

        fun onDataUnavailable()

    }

    interface ListListener<T> {

        fun onDataAvailable(listOfData: MutableList<T>)

        fun onDataUnavailable()

    }

    fun save(data: T)

    fun update(data: T)

    fun getData(itemId: String, listener: Listener<T>)

    fun getListOfData(listener: ListListener<T>)

    fun delete(data: T)
}