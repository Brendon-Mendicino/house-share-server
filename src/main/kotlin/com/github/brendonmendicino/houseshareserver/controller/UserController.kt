package com.github.brendonmendicino.houseshareserver.controller

import com.github.brendonmendicino.houseshareserver.dto.UserDto
import com.github.brendonmendicino.houseshareserver.service.UserService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) : CrudController<UserDto>(userService) {
    @GetMapping("/{userId}/groups")
    fun findGroup(@PathVariable userId: Long) = userService.findGroups(userId)

    @GetMapping("/logged")
    fun getLoggedUser(authentication: Authentication): UserDto {
        val principal = authentication.principal as? OidcUser ?: throw RuntimeException("User is not an OidcUser")

        return userService.findUserBySub(principal.subject)
    }
}