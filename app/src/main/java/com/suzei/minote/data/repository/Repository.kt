package com.suzei.minote.data.repository

import org.threeten.bp.OffsetDateTime

interface Repository<T> {

    interface Listener<T> {

        fun onDataAvailable(data: T)

        fun onDataUnavailable()

    }

    interface ListListener<T> {

        fun onDataAvailable(listOfData: MutableList<T>)

        fun onDataUnavailable()

    }

    interface ActionListener {

        fun onSuccess(itemId: String, createdDate: OffsetDateTime)

        fun onFailed()

    }

    fun save(data: T, actionListener: ActionListener)

    fun update(data: T)

    fun getData(itemId: String, listener: Listener<T>)

    fun getListOfData(listener: ListListener<T>)

    fun delete(data: T)
}