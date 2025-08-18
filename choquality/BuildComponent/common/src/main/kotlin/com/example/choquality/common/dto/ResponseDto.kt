package com.example.choquality.common.dto

data class ResponseDto<D>(
    var code: Int = 0,
    var msg: String? = null,
    var data: D? = null
)
