package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.*

@Entity
class Expense(
    var category: ExpenseCategory,
    var title: String,
    var description: String?,

    @ManyToOne
    var owner: AppUser,
    @ManyToOne
    var payer: AppUser,
    @ManyToOne
    @JoinColumn(updatable = false)
    var group: AppGroup,

    @OneToMany(mappedBy = "partOf", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    var expenseParts: MutableSet<ExpensePart> = mutableSetOf()
) : BaseEntity() {
    @Embedded
    lateinit var audit: Auditable

    /**
     * This amount represent cents. This conversion goes as:
     * `1.02 euro == 102 totalAmount`
     */
    val totalAmount: Long
        get() = expenseParts.sumOf { it.partAmount }

    fun update(other: Expense) {
        category = other.category
        title = other.title
        description = other.description
        owner = other.owner
        payer = other.payer
//        expenseParts = other.expenseParts
    }

    fun addExpensePart(part: ExpensePart) {
        expenseParts.add(part)
        part.partOf = this
    }

    fun removeExpensePart(part: ExpensePart) {
        expenseParts.remove(part)
    }
}

@Suppress("unused")
enum class ExpenseCategory {
    Car,
    Education,
    Training,
    ElectricBill,
    GasBill,
    Bill,
    Home,
    Restaurant,
    Games,
    FreeTime,
    Shopping,
    Travel,
    Rent,
    Meals,
    Others;
}