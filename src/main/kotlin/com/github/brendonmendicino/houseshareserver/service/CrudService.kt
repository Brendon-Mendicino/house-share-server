package com.github.brendonmendicino.houseshareserver.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CrudService<T : Any> {
    fun getAll(pageable: Pageable): Page<T>

    fun getById(id: Long): T

    fun save(dto: T): T

    fun update(id: Long, dto: T): T

    fun delete(id: Long)
}