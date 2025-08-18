package com.example.choquality.common.controller.view

import com.example.choquality.common.service.LoginService
import com.example.choquality.common.spec.StatusSpec
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

@RestController
@RequestMapping("/")
class LoginViewController(
    private val loginService: LoginService
) {
    private val log = LoggerFactory.getLogger(LoginViewController::class.java)


    @GetMapping
    fun login(
        @RequestParam(name = "status", required = false, defaultValue = "default") status: String
    ): ModelAndView {
        val authentication = SecurityContextHolder.getContext().authentication

        return if (authentication == null || authentication is AnonymousAuthenticationToken || !authentication.isAuthenticated) {
            ModelAndView("login/login").apply {
                addObject("status", if (status == "loginFail") StatusSpec.ERR_LOGIN else StatusSpec.DEFAULT)
            }
        } else {
            ModelAndView("redirect:/todo/main")
        }
    }

}