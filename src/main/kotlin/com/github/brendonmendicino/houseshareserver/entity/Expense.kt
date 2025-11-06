package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.*

@Entity
class Expense(
    var amount: Double,
    var category: ExpenseCategory,
    var title: String,
    var description: String?,

    @ManyToOne
    var owner: AppUser,
    @ManyToOne
    var payer: AppUser,
    @ManyToOne
    var group: AppGroup,

    @OneToMany(mappedBy = "partOf", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var expenseParts: MutableSet<ExpensePart> = mutableSetOf()
) : BaseEntity() {
    @Embedded
    lateinit var audit: Auditable

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