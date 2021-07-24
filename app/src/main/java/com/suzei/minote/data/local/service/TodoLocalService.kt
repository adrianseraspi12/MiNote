package com.suzei.minote.data.local.service

import com.suzei.minote.data.Result
import com.suzei.minote.data.local.dao.TodoDao
import com.suzei.minote.data.local.dao.TodoItemDao
import com.suzei.minote.data.local.entity.Todo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoLocalService(
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        private val todoDao: TodoDao,
        private val todoItemDao: TodoItemDao
) : LocalService<Todo> {

    companion object {
        fun instance(ioDispatcher: CoroutineDispatcher, todoDao: TodoDao, todoItemDao: TodoItemDao)
                : TodoLocalService = TodoLocalService(ioDispatcher, todoDao, todoItemDao)
    }

    override suspend fun save(data: Todo): Result<Todo> = withContext(ioDispatcher) {
        return@withContext try {
            todoDao.insertTodoWithTasks(data, todoItemDao)
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun update(data: Todo): Result<Nothing> = withContext(ioDispatcher) {
        return@withContext try {
            todoDao.updateTodoWithTasks(data, todoItemDao)
            Result.Success()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getData(itemId: String): Result<Todo> = withContext(ioDispatcher) {
        return@withContext try {
            val todo = todoDao.getTodoWithTasksById(itemId, todoItemDao)
            Result.Success(todo)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getListOfData(): Result<List<Todo>> = withContext(ioDispatcher) {
        return@withContext try {
            val listOfTodo = todoDao.getAllTodoWithTasks(todoItemDao)
            Result.Success(listOfTodo)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun delete(data: Todo): Result<Nothing> = withContext(ioDispatcher) {
        return@withContext try {
            todoDao.deleteTodoWithTasks(data, todoItemDao)
            Result.Success()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}