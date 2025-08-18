package com.example.choquality.common.mapper

import com.example.choquality.common.dto.UserRes
import com.example.choquality.common.jpa.entity.UserInfoEntity

fun UserInfoEntity.toRes() = UserRes(
    id = requireNotNull(this.id) { "User id is null" },
    email = requireNotNull(this.email) { "User email is null" },
    name = requireNotNull(this.email) { "User name is null" },
)