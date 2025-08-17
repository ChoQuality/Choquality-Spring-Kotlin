package com.example.choquality.common.service

interface LoginService {
    fun attemptLogin(email: String, password: String): String
    fun saveUser(email: String, password: String): Boolean
}