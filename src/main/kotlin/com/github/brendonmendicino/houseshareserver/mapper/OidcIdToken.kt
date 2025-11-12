package com.github.brendonmendicino.houseshareserver.mapper

import com.github.brendonmendicino.houseshareserver.entity.AppUser
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import java.net.URI

fun OidcIdToken.toUserEntity() = AppUser(
    username = preferredUsername,
    email = email,
    firstName = givenName,
    lastName = familyName,
    sub = subject,
    picture = runCatching { URI(picture) }.getOrNull()
)
