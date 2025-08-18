package com.example.choquality.todo.service

import com.example.choquality.common.jpa.entity.TodoInfoEntity

interface TodoService {
    fun getTodoList() : List<TodoInfoEntity>
    fun createTodo() : Boolean
}