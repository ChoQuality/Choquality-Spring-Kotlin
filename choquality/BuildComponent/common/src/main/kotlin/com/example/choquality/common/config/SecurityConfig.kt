package com.example.choquality.common.config


import com.example.choquality.common.filter.BearerFilter
import com.example.choquality.common.jwt.JWTComponent
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.context.NullSecurityContextRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.OncePerRequestFilter

@Configuration(proxyBeanMethods = false)
class SecurityConfig(
    private val httpSecurity: HttpSecurity,
    private val authenticationProvider: AuthenticationProvider,
    @Qualifier("JWTComponent") private val jwtComponent: JWTComponent
) {

    private val headerFilter: OncePerRequestFilter = BearerFilter(jwtComponent)

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(): SecurityFilterChain {
        httpSecurity
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authenticationProvider(authenticationProvider)
            .csrf { it.disable() }
            .headers { headers: HeadersConfigurer<HttpSecurity> ->
                headers.frameOptions { it.disable() }
            }
            .addFilterBefore(headerFilter, UsernamePasswordAuthenticationFilter::class.java)

            .securityContext {
                it.securityContextRepository(NullSecurityContextRepository())
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        HttpMethod.GET,
                        *arrayOf(
                            "/static/js/**",
                            "/static/css/**",
                            "/static/assets/**"
                        )
                    ).permitAll()
                    .requestMatchers(HttpMethod.GET).permitAll()
                    .requestMatchers(
                        HttpMethod.POST,*arrayOf("/login/**")
                    ).permitAll()

                    .requestMatchers(HttpMethod.OPTIONS).denyAll()
                    .requestMatchers(HttpMethod.POST).denyAll()
                    .requestMatchers(HttpMethod.PUT).denyAll()
                    .requestMatchers(HttpMethod.DELETE).denyAll()
            }

        return httpSecurity.build()
    }

    private fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            addAllowedOriginPattern("*")
            addAllowedHeader("*")
            addAllowedMethod("*")
            allowCredentials = true
            maxAge = 3600L
        }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
