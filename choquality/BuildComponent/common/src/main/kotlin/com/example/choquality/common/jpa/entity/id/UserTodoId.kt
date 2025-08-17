package com.example.choquality.common.jpa.entity.id

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class UserTodoId(
    @Column(name = "user_id")
    var userId: Int? = null,

    @Column(name = "todo_id")
    var todoId: Int? = null
) : Serializable

