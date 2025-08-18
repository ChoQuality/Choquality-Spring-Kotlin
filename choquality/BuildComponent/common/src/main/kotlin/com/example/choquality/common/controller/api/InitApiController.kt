package com.example.choquality.common.controller.api

import com.example.choquality.common.dto.ResponseDto
import com.example.choquality.common.jpa.repo.TodoInfoRepository
import com.example.choquality.common.jpa.repo.UserInfoRepository
import com.example.choquality.common.jpa.repo.UserTodoRepository
import com.example.choquality.common.service.LoginService
import com.example.choquality.common.spec.SDKSpec
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/init")
class InitApiController(
    private val userRepository: UserInfoRepository
    ,private val todoRepository: TodoInfoRepository
    ,private val userTodoRepository: UserTodoRepository

) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun init(): ResponseEntity<ResponseDto<Nothing>> {
        userRepository.deleteAll()
        todoRepository.deleteAll()
        userTodoRepository.deleteAll()

        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = null
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }
}