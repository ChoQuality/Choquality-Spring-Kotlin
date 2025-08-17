package com.example.choquality.common.jpa.entity

import com.example.choquality.common.jpa.entity.id.InfoId
import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.Comment

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_user")
open class UserInfoEntity (
    @EmbeddedId
    open var id: InfoId? = null,

    @Comment("유저 이름")
    @Column(name = "name")
    open var name: String? = null,

    @Comment("유저 이메일")
    @Column(name = "email", unique = true)
    open var email: String? = null,

    @Comment("유저 비밀번호")
    @Column(name = "password")
    open var password: String? = null

)