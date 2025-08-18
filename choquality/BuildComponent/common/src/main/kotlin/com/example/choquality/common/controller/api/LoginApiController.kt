package com.example.choquality.common.controller.api

import com.example.choquality.common.dto.LoginReq
import com.example.choquality.common.dto.ResponseDto
import com.example.choquality.common.dto.UserRes
import com.example.choquality.common.exception.SDKException
import com.example.choquality.common.jpa.entity.UserInfoEntity
import com.example.choquality.common.mapper.toRes
import com.example.choquality.common.service.LoginService
import com.example.choquality.common.spec.SDKSpec
import com.example.choquality.common.user.ChoqualityUser
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class LoginApiController(
    private val loginService: LoginService
) {
    private val log = LoggerFactory.getLogger(LoginApiController::class.java)

    @PostMapping(path=["/login"],produces = [MediaType.APPLICATION_JSON_VALUE])
    fun login(
        @RequestBody @Validated data: LoginReq
    ): ResponseEntity<ResponseDto<Nothing>> {

        val email = data.email ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        val password = data.password ?: throw SDKException(SDKSpec.FAIL_LOGIN)

        val loginToken = loginService.attemptLogin(email,password)

        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = null,
            access_token = loginToken

        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @PostMapping(path = ["/signup"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun signup(
        @RequestBody @Validated data: LoginReq
    ): ResponseEntity<ResponseDto<LoginReq>> {

        val email = data.email ?: throw SDKException(SDKSpec.FAIL_SIGNUP)
        val username = data.name ?: throw SDKException(SDKSpec.FAIL_SIGNUP)
        val password = data.password ?: throw SDKException(SDKSpec.FAIL_SIGNUP)

        loginService.signup(email,username,password)
        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = data
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @GetMapping(path = ["/me"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMe(
        @AuthenticationPrincipal user: ChoqualityUser
    ): ResponseEntity<ResponseDto<UserRes>> {

        val userId = user.loginInfo.id ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        val userInfoEntity = loginService.get(userId)
        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = userInfoEntity.toRes()
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

    @PutMapping(path = ["/me"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun putMe(
        @AuthenticationPrincipal user: ChoqualityUser,
        @RequestBody @Validated data: LoginReq
    ): ResponseEntity<ResponseDto<UserRes>> {

        val userId = user.loginInfo.id ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        val userInfoEntity = loginService.get(userId).apply {
            name = data.name
            password = data.password
        }

        loginService.put(userId,userInfoEntity)
        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = userInfoEntity.toRes()
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }
    @DeleteMapping(path = ["/me"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteMe(
        @AuthenticationPrincipal user: ChoqualityUser
    ): ResponseEntity<ResponseDto<Nothing>> {
        val userId = user.loginInfo.id ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        loginService.delete(userId)
        val body = ResponseDto(
            code = SDKSpec.SUCCESS.code,
            msg = SDKSpec.SUCCESS.message,
            data = null
        )
        return ResponseEntity.status(HttpStatus.OK).body(body)
    }

}