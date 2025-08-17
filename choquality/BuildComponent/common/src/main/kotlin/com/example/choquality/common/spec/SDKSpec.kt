package com.example.choquality.common.spec

import lombok.Getter

@Getter
enum class SDKSpec(val code: Int, val message: String) {
    SUCCESS(0, "SUCCESS"),
    FAIL_JWT_INIT(1, "JWT_INIT_FAILED"),
    JWT_EXPIRED(2, "JWT_EXPIRED"),
    FAIL_JWT_VALID(3, "JWT_VALID_FAIL"),
    FAIL_LOGIN(4, "LOGIN_FAIL")
}
