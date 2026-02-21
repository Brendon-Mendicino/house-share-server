package com.github.brendonmendicino.houseshareserver.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "service.paddle")
class PaddleProperties(
    var url: String,
)