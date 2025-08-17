package com.example.choquality.common.jwt

import com.auth0.jwt.JWTVerifier
import com.example.choquality.common.jpa.entity.UserInfoEntity

interface JWTComponent {
    fun initJWT(key: String)
    fun getJwtVerifier(): JWTVerifier
    fun createToken(userInfoEntity: UserInfoEntity): String
    fun checkToken(token: String): Boolean
    fun getLoginInfo(token: String): UserInfoEntity?
}