package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.Embeddable
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import java.time.OffsetDateTime

@Embeddable
class Auditable {
    @CreationTimestamp
    lateinit var createdAt: OffsetDateTime

    @UpdateTimestamp
    lateinit var updatedAt: OffsetDateTime

    @CreatedBy
    lateinit var createdBy: String

    @LastModifiedBy
    lateinit var lastModifiedBy: String
}