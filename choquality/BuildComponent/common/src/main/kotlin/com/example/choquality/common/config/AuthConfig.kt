package com.example.choquality.common.config

import com.example.choquality.common.jwt.JWTComponent
import com.example.choquality.common.service.LoginService
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

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
        passwordEncoder: PasswordEncoder
    ): LoginService =
        object : LoginService {

            override fun check(corporate_id: String, username: String): LoginInfoDto {
                val map = mutableMapOf<String, String>().apply {
                    put("corporate_id", corporate_id)
                    put("user_id", username)
                    put("use_flag", CommonConstant.USE)
                }
                val loginInfoDto = sqlSession.selectOne("LoginMapper.attemptLogin", map) as? LoginInfoDto
                return loginInfoDto ?: throw SDKException(SDKSpec.ERROR_LOGIN_ID)
            }

            override fun checkUser(company: String, username: String): LoginInfoDto {
                val map = mutableMapOf<String, String>().apply {
                    put("corporate_id", corporateId[company].toString())
                    put("user_id", username)
                    put("use_flag", CommonConstant.USE)
                }
                val loginInfoDto = sqlSession.selectOne("LoginMapper.attemptLogin", map) as? LoginInfoDto
                    ?: throw SDKException(SDKSpec.ERROR_LOGIN_ID)

                loginInfoDto.selectedDB = company
                val menu = attemptMenu(loginInfoDto)
                loginInfoDto.menuInfo = menu
                return loginInfoDto
            }

            override fun createDefaultCookie(): Cookie =
                Cookie(JWTConstant.CookieName, "").apply {
                    path = "/"
                    isHttpOnly = true
                    secure = true
                    maxAge = 60 * 60 * 24 // 24시간
                }

            private fun createAuthentication(loginInfoDto: LoginInfoDto, rawPassword: String): Authentication {
                return if (passwordEncoder.matches(rawPassword, loginInfoDto.userPw)) {
                    preAuthToken(loginInfoDto)
                } else {
                    PreAuthenticatedAuthenticationToken(loginInfoDto, "****")
                }
            }

            private fun preAuthToken(loginInfoDto: LoginInfoDto): PreAuthenticatedAuthenticationToken {
                loginInfoDto.userPw = "****"
                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                return PreAuthenticatedAuthenticationToken(loginInfoDto, "****", authorities)
            }

            override fun attemptLogin(loginInfoDto: LoginInfoDto?, password: String): Cookie {
                val cookie = createDefaultCookie()
                if (loginInfoDto == null) return cookie

                val auth = createAuthentication(loginInfoDto, password)
                val result = authenticationProvider.authenticate(auth)
                cookie.value = if (result.isAuthenticated) {
                    jwtComponent.createToken(loginInfoDto)
                } else {
                    ""
                }
                return cookie
            }

            override fun attemptMobileLogin(loginInfoDto: LoginInfoDto, password: String): LoginInfoDto {
                val auth = createAuthentication(loginInfoDto, password)
                val result = authenticationProvider.authenticate(auth)
                if (result.isAuthenticated) {
                    loginInfoDto.token = jwtComponent.createToken(loginInfoDto)
                    return loginInfoDto
                } else {
                    throw SDKException(SDKSpec.FAIL_LOGIN)
                }
            }

            override fun attemptMenu(loginInfoDto: LoginInfoDto): List<LoginMenuInfoDto> {
                val menus = sqlSession.selectList("LoginMapper.attemptMenu", loginInfoDto.userKey) as List<LoginMenuInfoDto>

                // 일반보고서 메뉴 제외
                if (!aiInterfaceService.isAiServiceEnabled(AiServiceCd.REPORT_NORMAL)) {
                    return menus.filterNot { it.menuExecutePath == "/todo/main/general" }
                }
                return menus
            }

            override fun getMainUrl(menuList: List<LoginMenuInfoDto>): String =
                menuList
                    .firstOrNull { it.menuLvl == 0 }
                    ?.menuExecutePath
                    ?: "/"

            override fun setLoginStatus(userDto: TblComUserDto) {
                comUserService.updateUserLoginStatus(userDto)
            }
        }
}