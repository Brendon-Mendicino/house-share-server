package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import java.net.URI

@Entity
class AppGroup(
    @NotEmpty
    @Column(nullable = false)
    var name: String,

    var description: String?,

    @Column(columnDefinition = "TEXT")
    var imageUrl: URI?,
) : BaseEntity() {
    @Embedded
    lateinit var audit: Auditable

    @ManyToMany(mappedBy = "groups")
    @JoinTable(name = "app_group_app_user")
    var users: MutableSet<AppUser> = mutableSetOf()

    @OneToMany(cascade = [CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH])
    var members: MutableSet<GroupMember> = mutableSetOf()

    @OneToMany(mappedBy = "group")
    var shoppingItems: MutableSet<ShoppingItem> = mutableSetOf()

    @OneToMany(mappedBy = "group")
    var expenses: MutableSet<Expense> = mutableSetOf()


    fun addUser(user: AppUser) {
        users.add(user)
        user.groups.add(this)
    }

    fun addMember(member: GroupMember) {
        this.members.add(member)
        member.group = this
    }

    fun removeUser(user: AppUser) {
        users.remove(user)
        user.groups.remove(this)
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