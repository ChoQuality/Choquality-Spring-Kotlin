package com.example.choquality.todo.service.impl

import com.example.choquality.common.jpa.entity.TodoInfoEntity
import com.example.choquality.common.jpa.repo.TodoInfoRepository
import com.example.choquality.todo.service.TodoService
import org.springframework.stereotype.Service

@Service
class TodoServiceImpl(
    private val todoInfoRepository: TodoInfoRepository
) : TodoService {

    override fun getTodoList(): List<TodoInfoEntity> {
        TODO("Not yet implemented")
    }

    override fun createTodo(): Boolean {
        TODO("Not yet implemented")
    }
}