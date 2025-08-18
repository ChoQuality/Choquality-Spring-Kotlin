package com.example.choquality.common.spec

import lombok.Getter

@Getter
enum class SDKSpec(val code: Int, val message: String) {
    SUCCESS(0, "SUCCESS"),
    FAIL_JWT_INIT(1, "JWT_INIT_FAILED"),
    JWT_EXPIRED(2, "JWT_EXPIRED"),
    FAIL_JWT_VALID(3, "JWT_VALID_FAIL"),
    FAIL_LOGIN(4, "LOGIN_FAIL"),
    FAIL_SAVE_USER(5, "FAIL_SAVE_USER"),
    FAIL_DELETE_USER(6, "FAIL_DELETE_USER"),
    FAIL_TODO_CREATE(7, "FAIL_TODO_CREATE"),
    FAIL_TODO_UPDATE(8, "FAIL_TODO_UPDATE"),
    FAIL_DB_PROCESS(99, "FAIL_DB_PROCESS")
}
