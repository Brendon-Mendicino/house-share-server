package com.github.brendonmendicino.houseshareserver

import com.github.brendonmendicino.houseshareserver.dto.*
import com.github.brendonmendicino.houseshareserver.entity.ExpenseCategory
import com.github.brendonmendicino.houseshareserver.entity.ShoppingItemPriority
import com.github.brendonmendicino.houseshareserver.service.GroupService
import com.github.brendonmendicino.houseshareserver.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.net.URI
import java.time.OffsetDateTime

@Component
@Profile("!prod")
class DbInitializer(
    private val userService: UserService,
    private val groupService: GroupService,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val role = "ROLE_admin"
        val authorities = AuthorityUtils.createAuthorityList(role)
        val authentication = UsernamePasswordAuthenticationToken("command_line_runner", role, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        userService.save(UserDto(0, "brendon", null, null, null, null))
        userService.save(
            UserDto(
                0,
                "flavy",
                null,
                null,
                null,
                URI("https://images.unsplash.com/photo-1612170153139-6f881ff067e0?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Y2hpY2tlbnxlbnwwfHwwfHx8MA%3D%3D&fm=jpg&q=60&w=3000")
            )
        )
        userService.save(UserDto(0, "salvo", null, null, null, null))
        userService.save(UserDto(0, "ciullo", null, null, null, null))
        userService.save(UserDto(0, "andrea", null, null, null, null))
        userService.save(UserDto(0, "peppe", null, null, null, null))

        groupService.save(GroupDto(0, "Belli", "ma io che ne so", listOf(1, 2, 3, 4, 5)))
        groupService.save(GroupDto(0, "Brutti", "mah", listOf(1, 2, 3)))
        groupService.save(GroupDto(0, "Cicci", "sisi", listOf(1, 3)))
        groupService.save(GroupDto(0, "NOOOOO", "tung tung", listOf(2, 3, 4, 5)))

        groupService.addShoppingItem(
            1, ShoppingItemDto(
                0, 1, 1, "Pizza", 1, null, ShoppingItemPriority.Later,
                OffsetDateTime.now(), null
            )
        )
        groupService.addShoppingItem(
            1, ShoppingItemDto(
                0, 1, 1, "pane", 1, null, ShoppingItemPriority.Later,
                OffsetDateTime.now(), null
            )
        )
        groupService.addShoppingItem(
            1, ShoppingItemDto(
                0, 2, 1, "patate", 1, null, ShoppingItemPriority.Later,
                OffsetDateTime.now(), null
            )
        )
        groupService.addShoppingItem(
            1, ShoppingItemDto(
                0, 3, 1, "cipolla", 1, null, ShoppingItemPriority.Later,
                OffsetDateTime.now(), null
            )
        )
        groupService.addShoppingItem(
            1, ShoppingItemDto(
                0, 1, 1, "aglio", 1, null, ShoppingItemPriority.Later,
                OffsetDateTime.now(), null
            )
        )
        groupService.addShoppingItem(
            1, ShoppingItemDto(
                0, 1, 1, "spazzola", 1, null, ShoppingItemPriority.Later,
                OffsetDateTime.now(), null
            )
        )
        groupService.addShoppingItem(
            1, ShoppingItemDto(
                0, 1, 1, "ciminiera", 1, null, ShoppingItemPriority.Later,
                OffsetDateTime.now(), null
            )
        )
        groupService.addShoppingItem(
            1, ShoppingItemDto(
                0, 1, 1, "100k 💶", 1, null, ShoppingItemPriority.Later,
                OffsetDateTime.now(), null
            )
        )

        groupService.checkShoppingItem(1, 6, CheckDto(1, OffsetDateTime.now()))
        groupService.checkShoppingItem(1, 7, CheckDto(1, OffsetDateTime.now()))
        groupService.checkShoppingItem(1, 8, CheckDto(2, OffsetDateTime.now()))

        groupService.addExpense(
            1, ExpenseDto(
                0, ExpenseCategory.Home, "patate", null, 1, 1, 1, OffsetDateTime.now(), listOf(
                    ExpensePartDto(0, 0, 1, 5),
                    ExpensePartDto(0, 0, 2, 5),
                )
            )
        )
        groupService.addExpense(
            1, ExpenseDto(
                0, ExpenseCategory.Home, "Cipulle", null, 1, 1, 1, OffsetDateTime.now(), listOf(
                    ExpensePartDto(0, 0, 2, 10),
                    ExpensePartDto(0, 0, 3, 10),
                )
            )
        )
        groupService.addExpense(
            1, ExpenseDto(
                0, ExpenseCategory.Home, "polpa", null, 1, 1, 1, OffsetDateTime.now(), listOf(
                    ExpensePartDto(0, 0, 1, 5),
                    ExpensePartDto(0, 0, 3, 5),
                )
            )
        )
    }
}