package com.github.brendonmendicino.houseshareserver

import com.github.brendonmendicino.houseshareserver.dto.GroupDto
import com.github.brendonmendicino.houseshareserver.dto.UserDto
import com.github.brendonmendicino.houseshareserver.service.GroupService
import com.github.brendonmendicino.houseshareserver.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DbInitializer(
    private val userService: UserService,
    private val groupService: GroupService
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        userService.save(UserDto(0, "brendon", null, null, null))
        userService.save(UserDto(0, "salvo", null, null, null))
        userService.save(UserDto(0, "flavy", null, null, null))
        userService.save(UserDto(0, "ciullo", null, null, null))
        userService.save(UserDto(0, "andrea", null, null, null))
        userService.save(UserDto(0, "peppe", null, null, null))

        groupService.save(GroupDto(0, "Belli", "ma io che ne so", listOf(1, 2, 3, 4, 5)))
        groupService.save(GroupDto(0, "Brutti", "mah", listOf(1, 2, 3)))
        groupService.save(GroupDto(0, "Cicci", "sisi", listOf(1, 3)))
        groupService.save(GroupDto(0, "NOOOOO", "tung tung", listOf(2, 3, 4, 5)))
    }
}