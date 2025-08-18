package com.example.choquality.common.service.impl

import com.example.choquality.common.exception.SDKException
import com.example.choquality.common.jpa.entity.UserInfoEntity
import com.example.choquality.common.jpa.repo.UserInfoRepository
import com.example.choquality.common.jwt.JWTComponent
import com.example.choquality.common.service.LoginService
import com.example.choquality.common.spec.SDKSpec
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.transaction.annotation.Transactional

open class LoginServiceImpl(
    private val authenticationProvider: AuthenticationProvider,
    private val jwtComponent: JWTComponent,
    private val passwordEncoder: PasswordEncoder,
    private val userInfoRepository: UserInfoRepository
) : LoginService {

    private fun createAuthentication(user: UserInfoEntity, rawPassword: String): Authentication =
        if (passwordEncoder.matches(rawPassword, user.password)) preAuthToken(user)
        else PreAuthenticatedAuthenticationToken(user, "****")

    private fun preAuthToken(user: UserInfoEntity): PreAuthenticatedAuthenticationToken {
        user.password = "****"
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        return PreAuthenticatedAuthenticationToken(user, "****", authorities)
    }

    private fun findByEmail(email: String): UserInfoEntity? {
        val probe = UserInfoEntity().apply { this.email = email }
        val matcher = ExampleMatcher.matching().withIgnoreNullValues().withIgnoreCase()
        val example = Example.of(probe, matcher)
        return userInfoRepository.findOne(example).orElse(null)
    }

    override fun attemptLogin(email: String, password: String): String {
        val user = findByEmail(email) ?: throw SDKException(SDKSpec.FAIL_LOGIN)
        val auth = authenticationProvider.authenticate(createAuthentication(user, password))
        if (!auth.isAuthenticated) throw SDKException(SDKSpec.FAIL_LOGIN)
        return jwtComponent.createToken(user)
    }

    @Transactional
    override fun saveUser(email: String, name: String, password: String): Boolean {
       val saveUser = userInfoRepository.save(
            UserInfoEntity().apply {
                this.email = email
                this.name = name
                this.password = passwordEncoder.encode(password)
            }
        )
        return saveUser.id != null
    }

    @Transactional
    override fun deleteAllUsers(): Boolean {
        userInfoRepository.deleteAll()
        return userInfoRepository.count() == 0L
    }
}
