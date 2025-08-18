package com.example.choquality.todo.controller.api

import com.example.choquality.common.dto.ResponseDto
import com.example.choquality.common.exception.SDKException
import com.example.choquality.common.spec.SDKSpec
import com.example.choquality.common.user.ChoqualityUser
import com.example.choquality.todo.service.TodoService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
    ): ResponseEntity<ResponseDto<String>> {
        user.
        val todoList  = todoService.getTodoList();
        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = ""
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun postTodos(
        @AuthenticationPrincipal user: ChoqualityUser,
        @RequestBody requestBody: Map<String, String>
    ): ResponseEntity<ResponseDto<String>> {
        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = ""
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @PutMapping(path = ["/{id}"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateTodo(
        @AuthenticationPrincipal user: ChoqualityUser,
        @PathVariable("id") id: Int,                   // ← path variable
        @RequestBody req: TodoUpdateReq
    ): ResponseEntity<ResponseDto<String>> {

        val updated = todoService.update(id, req)      // Boolean 가정
        return if (updated) {
            ResponseEntity.ok(ResponseDto(code = SDKSpec.SUCCESS.code, msg = SDKSpec.SUCCESS.message, data = "updated"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseDto(code = SDKSpec.FAIL_UPDATE_TODO.code, msg = "존재하지 않는 항목입니다.", data = null))
        }
    }

    @DeleteMapping(path = ["/{id}"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteTodo(
        @AuthenticationPrincipal user: ChoqualityUser,
        @PathVariable("id") id: Int,                   // ← path variable
        @RequestBody req: TodoUpdateReq
    ): ResponseEntity<ResponseDto<String>> {

        val updated = todoService.update(id, req)      // Boolean 가정
        return if (updated) {
            ResponseEntity.ok(ResponseDto(code = SDKSpec.SUCCESS.code, msg = SDKSpec.SUCCESS.message, data = "updated"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseDto(code = SDKSpec.FAIL_UPDATE_TODO.code, msg = "존재하지 않는 항목입니다.", data = null))
        }
    }

    @GetMapping(path = ["/search"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun searchTodo(
        @AuthenticationPrincipal user: ChoqualityUser,
        @RequestParam("id") id: Int
    ): ResponseEntity<ResponseDto<String>> {

        val updated = todoService.update(id, req)      // Boolean 가정
        return if (updated) {
            ResponseEntity.ok(ResponseDto(code = SDKSpec.SUCCESS.code, msg = SDKSpec.SUCCESS.message, data = "updated"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseDto(code = SDKSpec.FAIL_UPDATE_TODO.code, msg = "존재하지 않는 항목입니다.", data = null))
        }
    }

}