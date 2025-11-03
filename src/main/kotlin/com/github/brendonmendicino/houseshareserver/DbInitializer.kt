package com.github.brendonmendicino.houseshareserver

import com.github.brendonmendicino.houseshareserver.dto.CheckDto
import com.github.brendonmendicino.houseshareserver.dto.GroupDto
import com.github.brendonmendicino.houseshareserver.dto.ShoppingItemDto
import com.github.brendonmendicino.houseshareserver.dto.UserDto
import com.github.brendonmendicino.houseshareserver.entity.ShoppingItemPriority
import com.github.brendonmendicino.houseshareserver.service.GroupService
import com.github.brendonmendicino.houseshareserver.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class DbInitializer(
    private val userService: UserService,
    private val groupService: GroupService,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val role = "ROLE_admin"
        val authorities = AuthorityUtils.createAuthorityList(role)
        val authentication = UsernamePasswordAuthenticationToken("command_line_runner", role, authorities)
        SecurityContextHolder.getContext().authentication = authentication

        userService.save(UserDto(0, "brendon", null, null, null))
        userService.save(UserDto(0, "flavy", null, null, null))
        userService.save(UserDto(0, "salvo", null, null, null))
        userService.save(UserDto(0, "ciullo", null, null, null))
        userService.save(UserDto(0, "andrea", null, null, null))
        userService.save(UserDto(0, "peppe", null, null, null))

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
    }
}