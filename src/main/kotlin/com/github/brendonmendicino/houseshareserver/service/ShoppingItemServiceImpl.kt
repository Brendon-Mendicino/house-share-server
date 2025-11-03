package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.repository.ShoppingItemRepository
import com.github.brendonmendicino.houseshareserver.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ShoppingItemServiceImpl(
    private val shoppingItemRepository: ShoppingItemRepository,
    private val userRepository: UserRepository,
) : ShoppingItemService {
}