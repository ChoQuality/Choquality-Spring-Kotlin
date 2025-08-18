package com.example.choquality.todo.advise


import com.example.choquality.common.dto.ResponseDto
import com.example.choquality.common.exception.SDKException
import com.example.choquality.common.spec.SDKSpec
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = ["com.example.choquality.todo.controller.api"])
class TodoApiExceptionHandler {

    @ExceptionHandler(JpaSystemException::class)
    fun handleJpa(ex: JpaSystemException): ResponseEntity<ResponseDto<Nothing>> {
        val body = ResponseDto(code = SDKSpec.FAIL_DB_PROCESS.code, msg = SDKSpec.FAIL_DB_PROCESS.message, data = null)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body)
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class, MethodArgumentNotValidException::class, BindException::class])
    fun handleBadRequest(ex: Exception): ResponseEntity<ResponseDto<Nothing>> {
        val body = ResponseDto(code = SDKSpec.FAIL_PARAM.code, msg = SDKSpec.FAIL_PARAM.message, data = null)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(SDKException::class)
    fun handleSdk(ex: SDKException): ResponseEntity<ResponseDto<Nothing>> {
        val status = when (ex.code) {
            SDKSpec.FAIL_TODO_GET.code,SDKSpec.FAIL_TODO_UPDATE.code,SDKSpec.FAIL_TODO_DELETE.code -> HttpStatus.NOT_FOUND
            else -> HttpStatus.NOT_FOUND
        }
        val body = ResponseDto(code = ex.code, msg = ex.message, data = null)
        return ResponseEntity.status(status).body(body)
    }
}