package com.suzei.minote.data.repository

import com.suzei.minote.data.dao.TodoDao
import com.suzei.minote.data.entity.Todo
import com.suzei.minote.utils.LogMe
import com.suzei.minote.utils.executors.AppExecutor

class TodoRepository private constructor(
        private val appExecutor: AppExecutor,
        private val todoDao: TodoDao): Repository<Todo> {

    companion object {

        fun getInstance(appExecutor: AppExecutor, todoDao: TodoDao): TodoRepository {
            return TodoRepository(appExecutor, todoDao)
        }

    }

    override fun save(data: Todo) {
        LogMe.info("TodoRepository =  Save")
        val runnable = { todoDao.insertTodoWithTasks(data) }
        appExecutor.diskIO.execute(runnable)
    }

    override fun update(data: Todo) {

    }

    override fun getData(itemId: Int, listener: Repository.Listener<Todo>) {

        val runnable = {

            val todo = todoDao.getTodo(itemId)

            appExecutor.mainThread.execute {
                listener.onDataAvailable(todo)
            }

        }

        appExecutor.diskIO.execute(runnable)
    }

    override fun getListOfData(listener: Repository.ListListener<Todo>) {
        val runnable = {

            val listOfTodo = todoDao.getAllTodoWithTasks()

            appExecutor.mainThread.execute {

                if (listOfTodo.isEmpty()) {
                    listener.onDataUnavailable()
                }
                else {
                    listener.onDataAvailable(listOfTodo.toMutableList())
                }

            }

        }

        appExecutor.diskIO.execute(runnable)
    }

    override fun delete(data: Todo) {

    }

}