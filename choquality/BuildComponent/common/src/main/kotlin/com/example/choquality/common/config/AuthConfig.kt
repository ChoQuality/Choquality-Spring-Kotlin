package com.example.choquality.common.config

import com.example.choquality.common.exception.SDKException
import com.example.choquality.common.jpa.entity.UserInfoEntity
import com.example.choquality.common.jpa.repo.UserInfoRepository
import com.example.choquality.common.jwt.JWTComponent
import com.example.choquality.common.service.LoginService
import com.example.choquality.common.spec.SDKSpec
import jakarta.servlet.http.Cookie
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken

@Configuration
class AuthConfig(
    private val userInfoRepository: UserInfoRepository
) {



    @Bean
    fun userDetailsService(): UserDetailsService =
        UserDetailsService { username ->
            object : UserDetails {
                override fun getAuthorities(): Collection<GrantedAuthority> =
                    setOf(SimpleGrantedAuthority("ROLE_USER"))

                override fun getPassword(): String = "*****"
                override fun getUsername(): String = username

                // UserDetails 필수 메서드들
                override fun isAccountNonExpired(): Boolean = true
                override fun isAccountNonLocked(): Boolean = true
                override fun isCredentialsNonExpired(): Boolean = true
                override fun isEnabled(): Boolean = true
            }
        }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(): AuthenticationProvider =
        object : AuthenticationProvider {
            @Throws(AuthenticationException::class)
            override fun authenticate(authentication: Authentication): Authentication = authentication
            override fun supports(authentication: Class<*>): Boolean = true
        }

    @Bean
    fun loginService(
        authenticationProvider: AuthenticationProvider,
        jwtComponent: JWTComponent,
        passwordEncoder: PasswordEncoder
    ): LoginService =
        object : LoginService {

            private fun createAuthentication(loginInfoDto: UserInfoEntity, rawPassword: String): Authentication {
                return if (passwordEncoder.matches(rawPassword, loginInfoDto.password)) {
                    preAuthToken(loginInfoDto)
                } else {
                    PreAuthenticatedAuthenticationToken(loginInfoDto, "****")
                }
            }

            private fun preAuthToken(loginInfoDto: UserInfoEntity): PreAuthenticatedAuthenticationToken {
                loginInfoDto.password = "****"
                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                return PreAuthenticatedAuthenticationToken(loginInfoDto, "****", authorities)
            }

            private fun checkUser(email: String): UserInfoEntity? {
                fun findByUserId(email: String): UserInfoEntity? {
                    val probe = UserInfoEntity(
                        email=email
                    )
                    val matcher = ExampleMatcher.matching()
                        .withIgnoreNullValues()
                        .withIgnoreCase()

                    val example = Example.of(probe, matcher)
                    return userInfoRepository.findAll(example)[0]
                }
                return findByUserId(email)
            }

            override fun attemptLogin(email: String, password: String): String {
                val loginInfoDto = checkUser(email)
                if(loginInfoDto != null){
                    val auth = createAuthentication(loginInfoDto, password)
                    val result = authenticationProvider.authenticate(auth)
                    if (result.isAuthenticated) {
                        return jwtComponent.createToken(loginInfoDto)
                    } else {
                        throw SDKException(SDKSpec.FAIL_LOGIN)
                    }
                }
                throw SDKException(SDKSpec.FAIL_LOGIN)
            }

            override fun saveUser(email: String, password: String): Boolean {
                TODO("Not yet implemented")
            }
        }
}