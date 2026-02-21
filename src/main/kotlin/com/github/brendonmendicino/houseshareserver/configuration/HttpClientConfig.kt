package com.github.brendonmendicino.houseshareserver.configuration

import com.github.brendonmendicino.houseshareserver.service.PaddleService
import org.springframework.context.annotation.Configuration
import org.springframework.web.service.registry.ImportHttpServices

@Configuration
@ImportHttpServices(PaddleService::class)
class HttpClientConfig {
}