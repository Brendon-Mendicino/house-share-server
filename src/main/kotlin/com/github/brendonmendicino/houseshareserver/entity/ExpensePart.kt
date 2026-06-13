package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne


@Entity
class ExpensePart(
    /**
     * This amount represent cents. This conversion goes as:
     * `1.02 euro == 102 partAmount`
     */
    @Column(nullable = false)
    var partAmount: Long,

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    var partOf: Expense,

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    var memberPart: GroupMember,
) : BaseEntity()