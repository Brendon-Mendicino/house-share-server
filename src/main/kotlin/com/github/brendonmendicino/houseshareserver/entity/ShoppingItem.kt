package com.github.brendonmendicino.houseshareserver.entity

import com.github.brendonmendicino.houseshareserver.dto.ShoppingItemDto
import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.PositiveOrZero
import java.time.OffsetDateTime

@Entity
class ShoppingItem(
    @NotEmpty
    @Column(nullable = false)
    var name: String,
    var amount: Int,
    /**
     * Represent the money compact representation.
     * Where 1 euro == 100 price.
     */
    @PositiveOrZero
    var price: Long?,

    var priority: ShoppingItemPriority?,

    var checkoffTimestamp: OffsetDateTime?,

    @ManyToOne
    var checkingMember: GroupMember?,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    var owner: GroupMember,

    @ManyToOne
    @JoinColumn
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

    fun check(member: GroupMember, timestamp: OffsetDateTime) {
        checkoffTimestamp = timestamp
        checkingMember = member

        member.checkedShoppingItems.add(this)
    }

    fun uncheck() {
        checkoffTimestamp = null
        checkingMember?.checkedShoppingItems?.remove(this)
        checkingMember = null
    }
}

@Suppress("unused")
enum class ShoppingItemPriority {
    Now,
    Soon,
    Later;
}