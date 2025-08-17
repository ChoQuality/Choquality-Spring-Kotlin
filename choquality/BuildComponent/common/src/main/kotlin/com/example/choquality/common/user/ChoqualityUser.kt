package com.example.choquality.common.user

import com.example.choquality.common.jpa.entity.UserInfoEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class ChoqualityUser: User {
    val loginInfo: UserInfoEntity

    constructor(
        username: String,
        password: String,
        authorities: Collection<GrantedAuthority>,
        loginInfo: UserInfoEntity
    ) : super(username, password, authorities) {
        this.loginInfo = loginInfo
    }
    constructor(
        username: String,
        password: String,
        enabled: Boolean,
        accountNonExpired: Boolean,
        credentialsNonExpired: Boolean,
        accountNonLocked: Boolean,
        authorities: Collection<GrantedAuthority>,
        loginInfo: UserInfoEntity
    ) : super(
        username,
        password,
        enabled,
        accountNonExpired,
        credentialsNonExpired,
        accountNonLocked,
        authorities
    ) {
        this.loginInfo = loginInfo
    }
}