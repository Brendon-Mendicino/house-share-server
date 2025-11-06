package com.github.brendonmendicino.houseshareserver.entity

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
    var appGroup: AppGroup,
) : BaseEntity() {
    @Embedded
    lateinit var audit: Auditable

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