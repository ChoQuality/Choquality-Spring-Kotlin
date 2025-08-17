package com.example.choquality.common.jpa.entity.id

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.hibernate.annotations.Comment
import java.io.Serializable

@Embeddable
data class InfoId(
    @Comment("키 id")
    @Column(name = "id")
    var id: Int? = null
) : Serializable
