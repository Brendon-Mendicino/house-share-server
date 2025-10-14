package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne

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
) : BaseEntity() {
    @Embedded
    lateinit var audit: Auditable
}

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