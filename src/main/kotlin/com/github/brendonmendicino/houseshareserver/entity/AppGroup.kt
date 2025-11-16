package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.*

@Entity
class AppGroup(
    var name: String,
    var description: String?,
) : BaseEntity() {
    @Embedded
    lateinit var audit: Auditable

    @ManyToMany
    @JoinTable(name = "app_group_app_user")
    var users: MutableSet<AppUser> = mutableSetOf()

    @OneToMany(mappedBy = "group")
    var shoppingItems: MutableSet<ShoppingItem> = mutableSetOf()

    @OneToMany(mappedBy = "group")
    var expenses: MutableSet<Expense> = mutableSetOf()


    fun addUser(user: AppUser) {
        users.add(user)
        user.groups.add(this)
    }

    fun removeUser(userId: Long) {
        val user = users.find { it.id == userId }
        users.remove(user)
        user?.groups?.remove(this)
    }

    fun addShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItems.add(shoppingItem)
        shoppingItem.group = this
    }

    fun removeShoppingItem(shoppingItemId: Long) {
        shoppingItems.removeIf { it.id == shoppingItemId }
    }

    fun addExpense(expense: Expense) {
        expenses.add(expense)
        expense.group = this
    }
}