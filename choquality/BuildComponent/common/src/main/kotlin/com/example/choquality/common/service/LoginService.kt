package com.example.choquality.common.service

import com.example.choquality.common.jpa.entity.UserInfoEntity
import jakarta.servlet.http.Cookie

interface LoginService {
    fun checkUser(username: String?): UserInfoEntity?
    fun attemptLogin(loginInfoDto: UserInfoEntity?, password: String?): Cookie?
}