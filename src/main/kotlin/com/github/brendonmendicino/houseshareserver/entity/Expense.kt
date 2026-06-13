package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank

@Entity
class Expense(
    var category: ExpenseCategory?,

    @NotBlank
    @Column(nullable = false)
    var title: String,

    @Column(length = 5000)
    var description: String?,

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    var owner: GroupMember,

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    var payer: GroupMember,

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

// TODO: shall i put this inside the class??
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