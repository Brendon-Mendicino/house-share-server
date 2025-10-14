package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne

@Entity
class ShoppingItem(
    @ManyToOne(fetch = FetchType.EAGER)
    var owner: AppUser,
    @ManyToOne(fetch = FetchType.EAGER)
    var appGroup: AppGroup,
    var name: String,
    var amount: Int,
    var price: Double?,
    var priority: ShoppingItemPriority,
) : BaseEntity() {
    @Embedded
    lateinit var audit: Auditable
}

enum class ShoppingItemPriority {
    Now,
    Soon,
    Later;
}