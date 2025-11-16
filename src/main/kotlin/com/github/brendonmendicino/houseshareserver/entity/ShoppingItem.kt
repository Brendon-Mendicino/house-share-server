package com.github.brendonmendicino.houseshareserver.entity

import com.github.brendonmendicino.houseshareserver.dto.ShoppingItemDto
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import java.time.OffsetDateTime

@Entity
class ShoppingItem(
    var name: String,
    var amount: Int,
    var price: Double?,
    var priority: ShoppingItemPriority,

    var checkoffTimestamp: OffsetDateTime?,
    @ManyToOne
    var checkingUser: AppUser?,

    @ManyToOne(fetch = FetchType.EAGER)
    var owner: AppUser,
    @ManyToOne(fetch = FetchType.EAGER)
    var group: AppGroup,
) : BaseEntity() {
    @Embedded
    lateinit var audit: Auditable

    fun update(dto: ShoppingItemDto) {
        name = dto.name
        amount = dto.amount
        price = dto.price
        priority = dto.priority
    }

    fun check(user: AppUser, timestamp: OffsetDateTime) {
        checkoffTimestamp = timestamp
        checkingUser = user

        user.checkedShoppingItems.add(this)
    }

    fun uncheck() {
        checkoffTimestamp = null
        checkingUser?.checkedShoppingItems?.remove(this)
        checkingUser = null
    }
}

@Suppress("unused")
enum class ShoppingItemPriority {
    Now,
    Soon,
    Later;
}