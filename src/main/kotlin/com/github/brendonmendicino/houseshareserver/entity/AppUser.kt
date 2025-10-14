package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

@Entity
class AppUser(
    var username: String,
) : BaseEntity() {
    @CreationTimestamp
    lateinit var createdAt: OffsetDateTime

    @ManyToMany(mappedBy = "users", cascade = [CascadeType.MERGE])
    lateinit var groups: MutableMap<Long, AppGroup>

    @OneToMany(mappedBy = "owner", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    lateinit var shoppingItems: MutableMap<Long, ShoppingItem>

    @OneToMany(mappedBy = "owner", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    lateinit var ownedExpenses: MutableMap<Long, Expense>

    @OneToMany(mappedBy = "payer", cascade = [CascadeType.MERGE])
    lateinit var payedExpenses: MutableMap<Long, Expense>

    fun addShoppingItem(item: ShoppingItem) {
        shoppingItems[item.id] = item
        item.owner = this
    }

    fun addOwnedExpense(expense: Expense) {
        ownedExpenses[expense.id] = expense
        expense.owner = this
    }

    fun addPayedExpense(expense: Expense) {
        payedExpenses[expense.id] = expense
        expense.payer = this
    }
}