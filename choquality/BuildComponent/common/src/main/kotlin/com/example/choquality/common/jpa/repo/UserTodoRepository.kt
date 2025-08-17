package com.example.choquality.common.jpa.repo

import com.example.choquality.common.jpa.entity.UserTodoEntity
import com.example.choquality.common.jpa.entity.id.UserTodoId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserTodoRepository : JpaRepository<UserTodoEntity, UserTodoId> {
}