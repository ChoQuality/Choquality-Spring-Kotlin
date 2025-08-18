package com.example.choquality.todo.controller.api

import com.example.choquality.common.dto.ResponseDto
import com.example.choquality.common.exception.SDKException
import com.example.choquality.common.jpa.entity.TodoInfoEntity
import com.example.choquality.common.service.LoginService
import com.example.choquality.common.spec.SDKSpec
import com.example.choquality.common.user.ChoqualityUser
import com.example.choquality.todo.dto.TodoReq
import com.example.choquality.todo.dto.TodoRes
import com.example.choquality.todo.mapper.toRes
import com.example.choquality.todo.service.TodoService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/todos")
class TodoApiController(
    private val todoService: TodoService,
    private val loginService: LoginService
) {
    private val log = LoggerFactory.getLogger(TodoApiController::class.java)

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTodos(
        @AuthenticationPrincipal user: ChoqualityUser
    ): ResponseEntity<ResponseDto<List<TodoRes>>> {
        val userId = user.loginInfo.id ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        val todoList  = todoService.getTodoList(userId).map { it.toRes() }
        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = todoList
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun postTodos(
        @AuthenticationPrincipal user: ChoqualityUser,
        @RequestBody @Validated data: TodoReq
    ): ResponseEntity<ResponseDto<String>> {
        val userId = user.loginInfo.id ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        val userInfoEntity = loginService.get(userId)
        val todoEntity = TodoInfoEntity(title = data.title, content = data.content, writer = userInfoEntity.name)

        todoService.createTodo(userInfoEntity,todoEntity)

        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = SDKSpec.SUCCESS.message
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @GetMapping(path = ["/{todoId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTodo(
        @AuthenticationPrincipal user: ChoqualityUser,
        @PathVariable("todoId") todoId: Int
    ): ResponseEntity<ResponseDto<TodoRes>> {

        val userId = user.loginInfo.id ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        loginService.get(userId)
        val todoEntity = todoService.getTodo(userId,todoId)

        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = todoEntity.toRes()
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @PutMapping(path = ["/{todoId}"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateTodo(
        @AuthenticationPrincipal user: ChoqualityUser,
        @PathVariable("todoId") todoId: Int,                   // ← path variable
        @RequestBody @Validated data: TodoReq
    ): ResponseEntity<ResponseDto<TodoRes>> {

        val userId = user.loginInfo.id ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        val userInfoEntity = loginService.get(userId)
        val todoEntity = TodoInfoEntity(id=todoId,title = data.title, content = data.content, writer = userInfoEntity.name)
        todoService.updateTodo(userId,todoId,todoEntity)

        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = todoEntity.toRes()
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @DeleteMapping(path = ["/{todoId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteTodo(
        @AuthenticationPrincipal user: ChoqualityUser,
        @PathVariable("todoId") todoId: Int
    ): ResponseEntity<ResponseDto<Nothing>> {

        val userId = user.loginInfo.id ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        loginService.get(userId)

        todoService.deleteTodo(userId, todoId)

        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = null
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @GetMapping(path = ["/search"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun searchTodo(
        @AuthenticationPrincipal user: ChoqualityUser,
        @RequestParam("title") title: String?,
        @RequestParam("content") content: String?,
    ): ResponseEntity<ResponseDto<List<TodoRes>>> {

        val userId = user.loginInfo.id ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        loginService.get(userId)

        val todoList = todoService.searchTodo(userId,title,content).map { it.toRes() }
        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = todoList
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

}