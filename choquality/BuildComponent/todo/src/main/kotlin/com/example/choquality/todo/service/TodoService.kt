package com.example.choquality.todo.service

import com.example.choquality.common.jpa.entity.TodoInfoEntity

interface TodoService {
    fun getTodoList(userId : Int) : List<TodoInfoEntity>
    fun createTodo(userId : Int,todoInfo : TodoInfoEntity) : Boolean
    fun updateTodo(userId : Int,todoId : Int,todoInfo : TodoInfoEntity) : Boolean
    fun deleteTodo(userId : Int,todoId : Int) : Boolean
    fun searchTodo(userId : Int,todoId : Int) : TodoInfoEntity?
}