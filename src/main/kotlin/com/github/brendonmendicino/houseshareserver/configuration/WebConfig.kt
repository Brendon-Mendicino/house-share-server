package com.github.brendonmendicino.houseshareserver.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.data.web.config.EnableSpringDataWebSupport

/**
 * Article explaining why this is important
 * [link](https://stevenpg.com/posts/spring-data-page-impl-serialization-warning/)
 */
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
class WebConfig