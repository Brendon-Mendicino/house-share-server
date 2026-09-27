package com.github.brendonmendicino.houseshareserver.controller

import com.github.brendonmendicino.houseshareserver.configuration.Profiles
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/public")
@Profile(Profiles.SECURITY)
class PublicController(
    @param:Value($$"${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private val issuerUri: String,
) {
    @GetMapping("/account")
    fun redirectToAccount() = "redirect:$issuerUri/account"
}