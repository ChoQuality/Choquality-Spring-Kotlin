package com.example.choquality.common.jpa.repo

import com.example.choquality.common.jpa.entity.UserInfoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserInfoRepository : JpaRepository<UserInfoEntity, Int> {
}