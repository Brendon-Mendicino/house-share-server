package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne


@Entity
class ExpensePart(
    var partAmount: Double,

    @ManyToOne(fetch = FetchType.EAGER)
    var partOf: Expense,

    @ManyToOne
    var userPart: AppUser,
) : BaseEntity()