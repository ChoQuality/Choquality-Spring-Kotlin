package com.example.choquality.common.config

import com.example.choquality.common.jpa.repo.UserInfoRepository
import com.example.choquality.common.jwt.JWTComponent
import com.example.choquality.common.service.LoginService
import com.example.choquality.common.service.impl.LoginServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AuthConfig {



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
        passwordEncoder: PasswordEncoder,
        userInfoRepository: UserInfoRepository
    ): LoginService = LoginServiceImpl(authenticationProvider, jwtComponent, passwordEncoder,userInfoRepository)

}