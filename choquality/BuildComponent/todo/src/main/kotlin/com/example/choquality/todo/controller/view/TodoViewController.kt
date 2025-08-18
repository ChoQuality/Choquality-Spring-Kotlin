package com.example.choquality.common.controller.view

import com.example.choquality.common.service.LoginService
import com.example.choquality.common.spec.StatusSpec
import com.example.choquality.common.user.ChoqualityUser
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

@RestController
@RequestMapping("/todo/view")
class TodoViewController(
    private val loginService: LoginService
) {
    private val log = LoggerFactory.getLogger(TodoViewController::class.java)


    @GetMapping(path = ["/main"], produces = [APPLICATION_JSON_VALUE])
    fun todoMain(
        @AuthenticationPrincipal user : ChoqualityUser,
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