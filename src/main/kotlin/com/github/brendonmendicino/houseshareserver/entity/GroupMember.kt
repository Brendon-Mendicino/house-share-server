package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.*
import java.net.URI

@Entity
class GroupMember(
    @Column(nullable = false)
    var username: String,

    @Column(columnDefinition = "TEXT")
    var picture: URI?,

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    var group: AppGroup,

    @ManyToOne(fetch = FetchType.EAGER)
    var user: AppUser?,
) : BaseEntity() {
    @OneToMany(mappedBy = "memberPart")
    var memberExpenseParts: MutableSet<ExpensePart> = mutableSetOf()

    @OneToMany(mappedBy = "owner", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var shoppingItems: MutableSet<ShoppingItem> = mutableSetOf()

    @OneToMany(mappedBy = "checkingMember")
    var checkedShoppingItems: MutableSet<ShoppingItem> = mutableSetOf()

    @OneToMany(mappedBy = "owner", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var ownedExpenses: MutableSet<Expense> = mutableSetOf()

    @OneToMany(mappedBy = "payer", cascade = [CascadeType.MERGE])
    var payedExpenses: MutableSet<Expense> = mutableSetOf()


    fun addShoppingItem(item: ShoppingItem) {
        shoppingItems.add(item)
        item.owner = this
    }

    fun addOwnedExpense(expense: Expense) {
        ownedExpenses.add(expense)
        expense.owner = this
    }

    fun addPayedExpense(expense: Expense) {
        payedExpenses.add(expense)
        expense.payer = this
    }

}