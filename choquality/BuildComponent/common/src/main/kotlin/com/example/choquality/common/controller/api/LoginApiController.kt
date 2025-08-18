package com.example.choquality.common.controller.api

import com.example.choquality.common.dto.ResponseDto
import com.example.choquality.common.exception.SDKException
import com.example.choquality.common.service.LoginService
import com.example.choquality.common.spec.SDKSpec
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class LoginApiController(
    private val loginService: LoginService
) {
    private val log = LoggerFactory.getLogger(LoginApiController::class.java)

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun login(
        @RequestBody requestBody: Map<String, String>
    ): ResponseEntity<ResponseDto<String>> {

        val username = requestBody["email"] ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        val password = requestBody["password"]?: throw SDKException(SDKSpec.FAIL_LOGIN)

        val loginToken = loginService.attemptLogin(username,password)

        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = loginToken
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @PostMapping(path = ["/save"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun saveUser(
        @RequestBody requestBody: Map<String, String>
    ): ResponseEntity<ResponseDto<String>> {

        val email = requestBody["email"] ?: throw SDKException(SDKSpec.FAIL_SAVE_USER)
        val username = requestBody["name"] ?: email
        val password = requestBody["password"]?: throw SDKException(SDKSpec.FAIL_SAVE_USER)

        loginService.saveUser(email,username,password)
        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = SDKSpec.SUCCESS.message
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @GetMapping(path = ["/deleteAllUser"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteAllUser(): ResponseEntity<ResponseDto<String>> {

        loginService.deleteAllUsers()
        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = SDKSpec.SUCCESS.message
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }
}