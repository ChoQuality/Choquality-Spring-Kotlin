package com.example.choquality.common.exception


import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.NoHandlerFoundException

@ControllerAdvice
class GlobalControllerExceptionHandler {

    @ExceptionHandler(value = [NullPointerException::class, SecurityException::class])
    fun handleForbidden(): ModelAndView =
        ModelAndView("error/403").apply { status = HttpStatus.FORBIDDEN }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNotFound(): ModelAndView =
        ModelAndView("error/404").apply { status = HttpStatus.NOT_FOUND }

    @ExceptionHandler(Exception::class)
    fun handleAll(e: Exception): ModelAndView =
        ModelAndView("error/500").apply { status = HttpStatus.INTERNAL_SERVER_ERROR }
}