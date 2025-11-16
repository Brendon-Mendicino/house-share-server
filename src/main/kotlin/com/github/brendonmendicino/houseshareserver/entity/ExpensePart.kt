package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne


@Entity
class ExpensePart(
    /**
     * This amount represent cents. This conversion goes as:
     * `1.02 euro == 102 partAmount`
     */
    var partAmount: Long,

    @ManyToOne(fetch = FetchType.EAGER)
    var partOf: Expense,

    @ManyToOne
    var userPart: AppUser,
) : BaseEntity()