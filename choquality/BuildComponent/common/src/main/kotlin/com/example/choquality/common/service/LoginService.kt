package com.example.choquality.common.service

import com.example.choquality.common.jpa.entity.UserInfoEntity

interface LoginService {
    fun attemptLogin(email: String, password: String): String
    fun signup(email: String, name: String, password: String): Boolean
    fun delete(userId:Int): Boolean
    fun put(userId:Int,userInfo:UserInfoEntity): Boolean
    fun get(userId:Int): UserInfoEntity
}