package com.example.choquality.todo.controller.api

import com.example.choquality.common.dto.ResponseDto
import com.example.choquality.common.exception.SDKException
import com.example.choquality.common.jpa.entity.TodoInfoEntity
import com.example.choquality.common.spec.SDKSpec
import com.example.choquality.common.user.ChoqualityUser
import com.example.choquality.todo.dto.TodoReq
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
    private val todoService: TodoService
) {
    private val log = LoggerFactory.getLogger(TodoApiController::class.java)

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTodos(
        @AuthenticationPrincipal user: ChoqualityUser
    ): ResponseEntity<ResponseDto<List<TodoInfoEntity>>> {
        val id = user.loginInfo.id ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        val todoList  = todoService.getTodoList(id)
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
        val userName = user.loginInfo.name ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        val todoEntity = TodoInfoEntity(title = data.title, content = data.content, writer = userName)

        todoService.createTodo(userId,todoEntity)

        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = SDKSpec.SUCCESS.message
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @PutMapping(path = ["/{todoId}"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateTodo(
        @AuthenticationPrincipal user: ChoqualityUser,
        @PathVariable("todoId") todoId: Int,                   // ← path variable
        @RequestBody @Validated data: TodoReq
    ): ResponseEntity<ResponseDto<String>> {

        val userId = user.loginInfo.id ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        val userName = user.loginInfo.name ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        val todoEntity = TodoInfoEntity(title = data.title, content = data.content, writer = userName)
        todoService.updateTodo(userId,todoId,todoEntity)

        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = SDKSpec.SUCCESS.message
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @DeleteMapping(path = ["/{todoId}"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteTodo(
        @AuthenticationPrincipal user: ChoqualityUser,
        @PathVariable("todoId") todoId: Int
    ): ResponseEntity<ResponseDto<String>> {

        val userId = user.loginInfo.id ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        todoService.deleteTodo(userId, todoId)

        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = SDKSpec.SUCCESS.message
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @GetMapping(path = ["/search"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun searchTodo(
        @AuthenticationPrincipal user: ChoqualityUser,
        @RequestParam("todoId") todoId: Int
    ): ResponseEntity<ResponseDto<String>> {

        val userId = user.loginInfo.id ?: throw SDKException(SDKSpec.FAIL_LOGIN)

        val updated = todoService.update(id, req)      // Boolean 가정
        return if (updated) {
            ResponseEntity.ok(ResponseDto(code = SDKSpec.SUCCESS.code, msg = SDKSpec.SUCCESS.message, data = "updated"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseDto(code = SDKSpec.FAIL_UPDATE_TODO.code, msg = "존재하지 않는 항목입니다.", data = null))
        }
    }

}