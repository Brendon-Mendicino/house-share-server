package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

@Entity
@Table(
    indexes = [
        Index(columnList = "jti")
    ]
)
class AppUser(
    var username: String,
    var email: String?,
    var firstName: String?,
    var lastName: String?,
    /**
     * Json-web-Token ID. Specify if this user was created using
     * the Oauth2 authentication flow.
     */
    @Column(unique = true)
    var jti: String?,
) : BaseEntity() {
    @CreationTimestamp
    lateinit var createdAt: OffsetDateTime

    @ManyToMany(mappedBy = "users", cascade = [CascadeType.MERGE])
    var groups: MutableSet<AppGroup> = mutableSetOf()

    @OneToMany(mappedBy = "owner", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var shoppingItems: MutableSet<ShoppingItem> = mutableSetOf()

    @OneToMany(mappedBy = "checkingUser")
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