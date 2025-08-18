package com.example.choquality.todo.advise


import com.example.choquality.common.dto.ResponseDto
import com.example.choquality.common.exception.SDKException
import com.example.choquality.common.spec.SDKSpec
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.example.choquality.todo.controller.api"])
class TodoApiExceptionHandler {

    @ExceptionHandler(JpaSystemException::class)
    fun handleJpa(ex: JpaSystemException): ResponseEntity<ResponseDto<Nothing>> {
        val body = ResponseDto(code = SDKSpec.FAIL_DB_PROCESS.code, msg = SDKSpec.FAIL_DB_PROCESS.message, data = null)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body)
    }

    @ExceptionHandler(SDKException::class)
    fun handleSdk(ex: SDKException): ResponseEntity<ResponseDto<Nothing>> {
        val status = when (ex.code) {
            SDKSpec.FAIL_TODO_GET.code,SDKSpec.FAIL_TODO_UPDATE.code,SDKSpec.FAIL_TODO_DELETE.code -> HttpStatus.NOT_FOUND
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        val body = ResponseDto(code = ex.code, msg = ex.message, data = null)
        return ResponseEntity.status(status).body(body)
    }
}