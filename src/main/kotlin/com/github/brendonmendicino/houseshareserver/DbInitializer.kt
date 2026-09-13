package com.github.brendonmendicino.houseshareserver

import com.github.brendonmendicino.houseshareserver.dto.*
import com.github.brendonmendicino.houseshareserver.entity.ExpenseCategory
import com.github.brendonmendicino.houseshareserver.entity.ShoppingItemPriority
import com.github.brendonmendicino.houseshareserver.service.GroupService
import com.github.brendonmendicino.houseshareserver.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Pageable
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.net.URI
import java.time.OffsetDateTime


@Component
@Profile("dev")
class DbInitializer(
    private val userService: UserService,
    private val groupService: GroupService,
) : CommandLineRunner {

    override fun run(vararg args: String) {
        val role = "ROLE_admin"
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(
                "command_line_runner",
                role,
                AuthorityUtils.createAuthorityList(role)
            )

        if (userService.getAll(Pageable.ofSize(1)).totalPages != 0) {
            return
        }

        // Users
        val brendon = userService.save(
            AppUserDto(0, "brendon", null, null, null, null)
        )

        val flavy = userService.save(
            AppUserDto(
                0,
                "flavy",
                null,
                null,
                null,
                URI("https://images.unsplash.com/photo-1612170153139-6f881ff067e0?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Y2hpY2tlbnxlbnwwfHwwfHx8MA%3D%3D&fm=jpg&q=60&w=3000")
            )
        )

        val salvo = userService.save(
            AppUserDto(0, "salvo", null, null, null, null)
        )

        val ciullo = userService.save(
            AppUserDto(0, "ciullo", null, null, null, null)
        )

        val andrea = userService.save(
            AppUserDto(0, "andrea", null, null, null, null)
        )

        val peppe = userService.save(
            AppUserDto(0, "peppe", null, null, null, null)
        )

        // Groups
        val belli = groupService.save(
            AppGroupDto(
                id = 0,
                name = "Belli",
                description = "ma io che ne so",
                userIds = listOf(
                    brendon.id,
                    flavy.id,
                    salvo.id,
                    ciullo.id,
                    andrea.id,
                ),
                members = emptyList(),
                imageUrl = null,
            )
        )

        groupService.save(
            AppGroupDto(
                0,
                "Brutti",
                "mah",
                listOf(brendon.id, flavy.id, salvo.id),
                emptyList(),
                emptyList(),
                null,
            )
        )

        groupService.save(
            AppGroupDto(
                0,
                "Cicci",
                "sisi",
                listOf(brendon.id, salvo.id),
                emptyList(),
                emptyList(),
                null
            )
        )

        groupService.save(
            AppGroupDto(
                0,
                "NOOOOO",
                "tung tung",
                listOf(flavy.id, salvo.id, ciullo.id, andrea.id),
                emptyList(),
                emptyList(),
                null
            )
        )

        // Members created automatically when the group is created
        val members = belli.members.map { it.id }

        val (b, f, s) = members

        // Shopping items
        fun item(ownerId: Long, name: String) =
            ShoppingItemDto(
                id = 0,
                ownerId = ownerId,
                groupId = belli.id,
                name = name,
                amount = 1,
                price = null,
                priority = ShoppingItemPriority.Later,
                createdAt = OffsetDateTime.now(),
                check = null,
            )

        groupService.addShoppingItem(belli.id, item(b, "Pizza"))
        groupService.addShoppingItem(belli.id, item(b, "pane"))
        groupService.addShoppingItem(belli.id, item(f, "patate"))
        groupService.addShoppingItem(belli.id, item(s, "cipolla"))
        groupService.addShoppingItem(belli.id, item(b, "aglio"))

        val spazzola = groupService.addShoppingItem(belli.id, item(b, "spazzola"))
        val ciminiera = groupService.addShoppingItem(belli.id, item(b, "ciminiera"))
        val money = groupService.addShoppingItem(belli.id, item(b, "100k 💶"))

        groupService.checkShoppingItem(
            belli.id,
            spazzola.id,
            CheckDto(b, OffsetDateTime.now())
        )

        groupService.checkShoppingItem(
            belli.id,
            ciminiera.id,
            CheckDto(b, OffsetDateTime.now())
        )

        groupService.checkShoppingItem(
            belli.id,
            money.id,
            CheckDto(f, OffsetDateTime.now())
        )

        // Expenses
        fun expense(
            title: String,
            ownerId: Long,
            payerId: Long,
            vararg parts: Pair<Long, Long>,
        ) = ExpenseDto(
            id = 0,
            category = ExpenseCategory.Home,
            title = title,
            description = null,
            ownerId = ownerId,
            payerId = payerId,
            groupId = belli.id,
            expenseParts = parts.map {
                ExpensePartDto(
                    id = 0,
                    expenseId = 0,
                    memberId = it.first,
                    partAmount = it.second
                )
            },
            createdAt = OffsetDateTime.now(),
        )

        groupService.addExpense(
            belli.id,
            expense(
                "patate",
                b,
                b,
                b to 5,
                f to 5
            )
        )

        groupService.addExpense(
            belli.id,
            expense(
                "Cipulle",
                b,
                b,
                f to 10,
                s to 10
            )
        )

        groupService.addExpense(
            belli.id,
            expense(
                "polpa",
                b,
                b,
                b to 5,
                s to 5
            )
        )
    }
}