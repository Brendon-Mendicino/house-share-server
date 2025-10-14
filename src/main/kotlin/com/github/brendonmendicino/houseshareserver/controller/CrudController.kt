package com.github.brendonmendicino.houseshareserver.controller

import com.github.brendonmendicino.houseshareserver.service.CrudService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

sealed class CrudController<T : Any>(val crudService: CrudService<T>) {
    @GetMapping
    fun getAll(pageable: Pageable) = crudService.getAll(pageable)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = crudService.getById(id)

    @PostMapping
    fun save(@Valid @RequestBody dto: T) = crudService.save(dto)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody dto: T) = crudService.update(id, dto)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = crudService.delete(id)
}