package com.example.choquality.todo.mapper

import com.example.choquality.common.jpa.entity.TodoInfoEntity
import com.example.choquality.todo.dto.TodoRes

fun TodoInfoEntity.toRes() = TodoRes(
    id = requireNotNull(this.id) { "Todo id is null" },
    title = requireNotNull(this.title) { "Title is null" },
    content = requireNotNull(this.content) { "Content is null" },
    writer = requireNotNull(this.writer)
)