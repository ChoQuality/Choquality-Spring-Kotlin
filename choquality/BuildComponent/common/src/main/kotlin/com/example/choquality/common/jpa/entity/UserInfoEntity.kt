package com.example.choquality.common.jpa.entity

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_user")
open class UserInfoEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    open var id: Int? = null,

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