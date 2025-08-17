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
@Table(name = "tb_todo")
open class TodoInfoEntity (
    @EmbeddedId
    open var id: InfoId? = null,

    @Comment("todo title")
    @Column(name = "title")
    open var title: String? = null,

    @Comment("todo writer")
    @Column(name = "writer")
    open var writer: String? = null
)