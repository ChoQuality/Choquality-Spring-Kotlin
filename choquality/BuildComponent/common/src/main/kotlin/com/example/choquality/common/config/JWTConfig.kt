package com.example.choquality.common.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.choquality.common.constant.JWTConstant
import com.example.choquality.common.exception.SDKException
import com.example.choquality.common.jpa.entity.UserInfoEntity
import com.example.choquality.common.jwt.JWTComponent
import com.example.choquality.common.spec.SDKSpec
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.time.Instant

@Configuration(proxyBeanMethods = false)
class JWTConfig(
    private val environment: Environment
) {

    @Bean("JWTComponent")
    fun jwtComponent(): JWTComponent {
        val component = object : JWTComponent {
            private var jwtSecret: String = ""
            private lateinit var algorithm: Algorithm
            private lateinit var jwtVerifier: JWTVerifier

            override fun initJWT(key: String) {
                if (jwtSecret.isEmpty()) {
                    jwtSecret = requireNotNull(environment.getProperty(key)) {
                        "JWT secret not found for key: $key"
                    }
                    algorithm = Algorithm.HMAC256(jwtSecret)
                    jwtVerifier = JWT.require(algorithm).build()
                } else {
                    throw SDKException(SDKSpec.FAIL_JWT_INIT)
                }
            }

            override fun getJwtVerifier(): JWTVerifier = jwtVerifier

            override fun createToken(userInfoEntity: UserInfoEntity): String {
                val now = Instant.now()
                return JWT.create()
                    .withIssuer(JWTConstant.IssuerServer)
                    .withSubject(JWTConstant.Subject)
                    .withClaim(JWTConstant.Claim_User_Id, userInfoEntity.id)
                    .withClaim(JWTConstant.Claim_User_Name, userInfoEntity.name)
                    .withIssuedAt(now)
                    .withExpiresAt(now.plusSeconds(JWTConstant.Twelve_hour_second))
                    .sign(algorithm)
            }

            override fun checkToken(token: String): Boolean {
                return try {
                    val decoded = jwtVerifier.verify(token)
                    validateToken(decoded)
                } catch (e: io.jsonwebtoken.ExpiredJwtException) {
                    throw SDKException(SDKSpec.FAIL_JWT_VALID)
                } catch (e: TokenExpiredException) {
                    throw SDKException(SDKSpec.FAIL_JWT_VALID)
                } catch (e: Exception) {
                    throw SDKException(SDKSpec.FAIL_JWT_VALID)
                }
            }

            override fun getLoginInfo(token: String): UserInfoEntity {
                val decoded = JWT.decode(token)

                return UserInfoEntity(
                    id = decoded.getClaim(JWTConstant.Claim_User_Id).asInt(),
                    name = decoded.getClaim(JWTConstant.Claim_User_Name).asString(),
                    password = "*****"
                )
            }

            private fun validateToken(decoded: DecodedJWT): Boolean {
                return decoded.issuer == JWTConstant.IssuerServer &&
                        decoded.subject == JWTConstant.Subject &&
                        !decoded.getClaim(JWTConstant.Claim_User_Id).isNull &&
                        !decoded.getClaim(JWTConstant.Claim_User_Name).isNull
            }
        }

        component.initJWT("choquality.jwt.secret")
        return component
    }
}
