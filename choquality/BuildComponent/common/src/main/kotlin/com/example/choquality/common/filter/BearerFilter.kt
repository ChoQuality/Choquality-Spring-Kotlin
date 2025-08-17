package com.example.choquality.common.filter

import com.example.choquality.common.jwt.JWTComponent
import com.example.choquality.common.constant.JWTConstant
import com.example.choquality.common.user.ChoqualityUser
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class BearerFilter(
    private val jwtComponent: JWTComponent
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = getToken(request)

        if (token != null && jwtComponent.checkToken(token)) {
            val loginInfo = jwtComponent.getLoginInfo(token)
            if (loginInfo != null) {
                val choqualityUser = loginInfo.name?.let {
                    ChoqualityUser(
                        it,
                        "****",
                        setOf(SimpleGrantedAuthority("ROLE_USER")),
                        loginInfo
                    )
                }

                val context = SecurityContextHolder.getContext()
                if (choqualityUser != null) {
                    context.authentication = PreAuthenticatedAuthenticationToken(
                        choqualityUser,
                        choqualityUser.password,
                        choqualityUser.authorities
                    )
                }
            }
        }

        try {
            filterChain.doFilter(request, response)
        } finally {
            SecurityContextHolder.clearContext()
        }
    }

    private fun getToken(request: HttpServletRequest): String? {
        val header = request.getHeader(JWTConstant.Header_Authorization) ?: return null
        if (header.isBlank()) return null

        val matcher = JWTConstant.Patten_Bearer.matcher(header)
        return if (matcher.find()) matcher.group(1) else null
    }
}