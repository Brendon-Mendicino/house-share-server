package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

@Entity
class AppGroup(
    var name: String,
    var description: String?,
) : BaseEntity() {
    @CreationTimestamp
    lateinit var createdAt: OffsetDateTime

    @ManyToMany
    var users: MutableMap<Long, AppUser> = mutableMapOf()

    @OneToMany(mappedBy = "appGroup")
    var shoppingItems: MutableMap<Long, ShoppingItem> = mutableMapOf()

    @OneToMany(mappedBy = "group")
    var expenses: MutableMap<Long, Expense> = mutableMapOf()


    fun addUser(user: AppUser) {
        users[user.id] = user
        user.groups[id] = this
    }

    fun removeUser(userId: Long) {
        val user = users.remove(userId)
        user?.groups?.remove(id)
    }

    fun addShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems[shoppingItem.id] = shoppingItem
        shoppingItem.appGroup = this
    }

    fun removeShoppingItem(shoppingItemId: Long) {
        shoppingItems.remove(shoppingItemId)
    }

    fun addExpense(expense: Expense) {
        expenses[expense.id] = expense
        expense.group = this
    }
}