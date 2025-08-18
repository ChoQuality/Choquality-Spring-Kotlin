package com.example.choquality.todo.service

import com.example.choquality.common.jpa.entity.TodoInfoEntity

interface TodoService {
    fun getTodoList() : List<TodoInfoEntity>
    fun createTodo(todoInfo : TodoInfoEntity) : Boolean
    fun updateTodo(todoInfo : TodoInfoEntity) : Boolean
    fun deleteTodo(id : Int) : TodoInfoEntity
    fun searchTodo(id : Int) : TodoInfoEntity
}