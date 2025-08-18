package com.example.choquality.todo.service.impl

import com.example.choquality.common.jpa.entity.TodoInfoEntity
import com.example.choquality.common.jpa.repo.TodoInfoRepository
import com.example.choquality.todo.service.TodoService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TodoServiceImpl(
    private val todoInfoRepository: TodoInfoRepository
) : TodoService {

    override fun getTodoList(): List<TodoInfoEntity> {
        return todoInfoRepository.findAll()
    }

    @Transactional
    override fun createTodo(todoInfo:TodoInfoEntity): Boolean {
        val savedTodo = todoInfoRepository.save(todoInfo)
        return savedTodo.id != null
    }

    override fun updateTodo(todoInfo: TodoInfoEntity): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteTodo(id: Int): TodoInfoEntity {
        TODO("Not yet implemented")
    }

    override fun searchTodo(id: Int): TodoInfoEntity {
        TODO("Not yet implemented")
    }
}