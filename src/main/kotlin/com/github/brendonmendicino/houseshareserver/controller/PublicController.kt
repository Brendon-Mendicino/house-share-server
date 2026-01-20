package com.github.brendonmendicino.houseshareserver.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/public")
class PublicController(
    @param:Value("\${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private val issuerUri: String,
) {
    @GetMapping("/account")
    fun redirectToAccount() = "redirect:$issuerUri/account"
}