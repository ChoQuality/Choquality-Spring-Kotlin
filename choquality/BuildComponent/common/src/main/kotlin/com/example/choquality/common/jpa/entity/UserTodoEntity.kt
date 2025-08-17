package com.example.choquality.common.jpa.entity

import com.example.choquality.common.jpa.entity.id.UserTodoId
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_user_todo")
open class UserTodoEntity(

    @EmbeddedId
    open var id: UserTodoId? = null,

    @field:JsonIgnore
    @ManyToOne(fetch = LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    open var user: UserInfoEntity? = null,

    @field:JsonIgnore
    @ManyToOne(fetch = LAZY)
    @MapsId("todoId")
    @JoinColumn(name = "todo_id", referencedColumnName = "id")
    open var todo: TodoInfoEntity? = null
)
