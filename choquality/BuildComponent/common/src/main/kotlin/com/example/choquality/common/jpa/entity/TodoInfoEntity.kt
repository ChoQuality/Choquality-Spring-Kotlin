package com.example.choquality.common.jpa.entity

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_todo")
open class TodoInfoEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    open var id: Int? = null,

    @Comment("todo title")
    @Column(name = "title")
    open var title: String? = null,

    @Comment("todo content")
    @Column(name = "title")
    open var content: String? = null,

    @Comment("todo writer")
    @Column(name = "writer")
    open var writer: String? = null
)