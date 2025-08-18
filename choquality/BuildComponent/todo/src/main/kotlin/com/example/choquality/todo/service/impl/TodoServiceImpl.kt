package com.example.choquality.todo.service.impl

import com.example.choquality.common.exception.SDKException
import com.example.choquality.common.jpa.entity.TodoInfoEntity
import com.example.choquality.common.jpa.entity.UserInfoEntity
import com.example.choquality.common.jpa.entity.UserTodoEntity
import com.example.choquality.common.jpa.entity.id.UserTodoId
import com.example.choquality.common.jpa.repo.TodoInfoRepository
import com.example.choquality.common.jpa.repo.UserTodoRepository
import com.example.choquality.common.spec.SDKSpec
import com.example.choquality.todo.service.TodoService
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TodoServiceImpl(
    private val todoInfoRepository: TodoInfoRepository
    ,private val userTodoRepository: UserTodoRepository
) : TodoService {
    override fun getTodoList(userId: Int): List<TodoInfoEntity> {
        val probe = UserTodoEntity().apply {
            id = UserTodoId(userId = userId)
        }
        val matcher = ExampleMatcher.matchingAll()
            .withIgnoreNullValues()
            .withIgnoreCase()

        val example = Example.of(probe, matcher)
        val todos: List<TodoInfoEntity> =
            userTodoRepository.findAll(example)
                .mapNotNull { it.todo }

        return todos
    }

    override fun getTodo(userId: Int, todoId: Int): TodoInfoEntity {
        val probe = UserTodoEntity().apply {
            id = UserTodoId(userId = userId, todoId = todoId)
        }
        val matcher = ExampleMatcher.matchingAll()
            .withIgnoreNullValues()
            .withIgnoreCase()
        val example = Example.of(probe, matcher)

        val userTodo = userTodoRepository.findOne(example)
            .orElseThrow { SDKException(SDKSpec.FAIL_TODO_GET) }

        return userTodo.todo ?: throw SDKException(SDKSpec.FAIL_TODO_GET)
    }

    @Transactional
    override fun createTodo(userInfo : UserInfoEntity, todoInfo: TodoInfoEntity): Boolean {
        val savedTodo = todoInfoRepository.save(todoInfo)
        val savedUserTodo = userTodoRepository.save(UserTodoEntity().apply {
            id =  UserTodoId(userId = userInfo.id, todoId = savedTodo.id)
            user = userInfo
            todo = savedTodo
        })
        return savedUserTodo.id != null
    }

    @Transactional
    override fun updateTodo(userId: Int, todoId: Int, todoInfo: TodoInfoEntity): Boolean {
        val probe = UserTodoEntity().apply {
            id = UserTodoId(userId = userId, todoId = todoId)
        }
        val matcher = ExampleMatcher.matchingAll()
            .withIgnoreNullValues()
            .withIgnoreCase()

        val example = Example.of(probe, matcher)
        if(userTodoRepository.findOne(example).isPresent)
            todoInfoRepository.save(todoInfo)
        else
            throw SDKException(SDKSpec.FAIL_TODO_UPDATE)
        return true
    }

    @Transactional
    override fun deleteTodo(userId: Int, todoId: Int): Boolean {
        val probe = UserTodoEntity().apply {
            id = UserTodoId(userId = userId, todoId = todoId)
        }
        val matcher = ExampleMatcher.matchingAll()
            .withIgnoreNullValues()
            .withIgnoreCase()

        val example = Example.of(probe, matcher)
        if(userTodoRepository.findOne(example).isPresent){
            todoInfoRepository.deleteById(todoId)
            userTodoRepository.deleteById(UserTodoId(userId, todoId))
        } else
            throw SDKException(SDKSpec.FAIL_TODO_DELETE)
        return true
    }

    override fun searchTodo(userId : Int,title : String?,content : String?): List<TodoInfoEntity> {
        val probe = TodoInfoEntity().apply {
            this.title = title
            this.content = content
        }
        val matcher = ExampleMatcher.matchingAll()
            .withIgnoreNullValues()
            .withIgnoreCase()
            .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains())   // LIKE %title%
            .withMatcher("content", ExampleMatcher.GenericPropertyMatchers.contains()) // LIKE %content%
        val example = Example.of(probe, matcher)

        val likeMatched: List<TodoInfoEntity> = todoInfoRepository.findAll(example)
        if (likeMatched.isEmpty()) return emptyList()

        val matchedIds = likeMatched.mapNotNull { it.id }.toSet()
        if (matchedIds.isEmpty()) return emptyList()

        val myUserTodos: List<UserTodoEntity> =
            userTodoRepository.findAllByIdUserIdAndIdTodoIdIn(userId, matchedIds)

        return myUserTodos.mapNotNull { it.todo }



    }

}