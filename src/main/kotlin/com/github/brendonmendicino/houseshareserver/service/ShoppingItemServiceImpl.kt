package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.CheckDto
import com.github.brendonmendicino.houseshareserver.exception.ShoppingItemException
import com.github.brendonmendicino.houseshareserver.exception.UserException
import com.github.brendonmendicino.houseshareserver.repository.ShoppingItemRepository
import com.github.brendonmendicino.houseshareserver.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ShoppingItemServiceImpl(
    private val shoppingItemRepository: ShoppingItemRepository,
    private val userRepository: UserRepository,
) : ShoppingItemService {
    override fun checkShoppingItem(shoppingItemId: Long, dto: CheckDto): CheckDto {
        val shoppingItem = shoppingItemRepository.findByIdOrNull(shoppingItemId)
            ?: throw ShoppingItemException.NotFound.from(shoppingItemId)

        val user =
            userRepository.findByIdOrNull(dto.checkingUserId) ?: throw UserException.NotFound.from(dto.checkingUserId)

        shoppingItem.check(user, dto.checkoffTimestamp)

        return shoppingItemRepository.save(shoppingItem)
            .let { CheckDto(it.checkingUser!!.id, it.checkoffTimestamp!!) }
    }

    override fun uncheckShoppingItem(shoppingItemId: Long) {
        val shoppingItem = shoppingItemRepository.findByIdOrNull(shoppingItemId) ?: return

        shoppingItem.uncheck()
        shoppingItemRepository.save(shoppingItem)
    }
}