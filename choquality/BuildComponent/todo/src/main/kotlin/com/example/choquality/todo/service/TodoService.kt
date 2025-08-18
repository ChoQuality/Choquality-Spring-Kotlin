package com.example.choquality.todo.service

import com.example.choquality.common.jpa.entity.TodoInfoEntity
import com.example.choquality.common.jpa.entity.UserInfoEntity

interface TodoService {
    fun getTodoList(userId : Int) : List<TodoInfoEntity>
    fun getTodo(userId : Int,todoId : Int) : TodoInfoEntity
    fun createTodo(userInfo : UserInfoEntity,todoInfo : TodoInfoEntity) : Boolean
    fun updateTodo(userId : Int,todoId : Int,todoInfo : TodoInfoEntity) : Boolean
    fun deleteTodo(userId : Int,todoId : Int) : Boolean
    fun searchTodo(userId : Int,title : String?,content : String?) : List<TodoInfoEntity>
}