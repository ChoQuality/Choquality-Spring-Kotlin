package com.example.choquality.common.constant

import java.util.regex.Pattern

object JWTConstant {
    const val IssuerServer: String = "Proxy"
    const val Subject: String = "jwt-token"
    const val Header_Authorization: String = "Authorization"
    val Patten_Bearer: Pattern = Pattern.compile("^Bearer (.+?)$")
    const val Claim_User_Id: String = "User_Id"
    const val Claim_User_Name: String = "User_NM"
    const val Twelve_hour_second: Long = 3600 * 12L
}
