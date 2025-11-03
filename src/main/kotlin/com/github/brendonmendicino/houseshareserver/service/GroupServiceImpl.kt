package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.*
import com.github.brendonmendicino.houseshareserver.entity.AppGroup
import com.github.brendonmendicino.houseshareserver.entity.Expense
import com.github.brendonmendicino.houseshareserver.entity.ExpensePart
import com.github.brendonmendicino.houseshareserver.entity.ShoppingItem
import com.github.brendonmendicino.houseshareserver.exception.GroupException
import com.github.brendonmendicino.houseshareserver.exception.ShoppingItemException
import com.github.brendonmendicino.houseshareserver.exception.UserException
import com.github.brendonmendicino.houseshareserver.mapper.toDto
import com.github.brendonmendicino.houseshareserver.repository.ExpenseRepository
import com.github.brendonmendicino.houseshareserver.repository.GroupRepository
import com.github.brendonmendicino.houseshareserver.repository.ShoppingItemRepository
import com.github.brendonmendicino.houseshareserver.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GroupServiceImpl(
    private val groupRepository: GroupRepository,
    private val shoppingItemRepository: ShoppingItemRepository,
    private val expenseRepository: ExpenseRepository,
    private val userRepository: UserRepository,
) : GroupService {
    private fun checkUserInGroup(groupId: Long, userId: Long) {
        if (!groupRepository.existsUserById(groupId, userId))
            throw GroupException.NotMember.from(groupId, userId)
    }

    private fun checkUsersInGroup(groupId: Long, userIds: List<Long>) {
        for (userId in userIds) {
            checkUserInGroup(groupId, userId)
        }
    }

    /**
     * Creates a [AppGroup] with its users.
     */
    private fun createGroup(dto: GroupDto): AppGroup {
        val entity = AppGroup(
            name = dto.name,
            description = dto.description,
        )

        dto
            .userIds
            .map { userRepository.findByIdOrNull(it) ?: throw UserException.NotFound.from(it) }
            .forEach { entity.addUser(it) }

        return entity
    }

    /**
     * Creates [ShoppingItem] with its dependencies
     */
    private fun createShoppingItem(groupId: Long, itemDto: ShoppingItemDto): ShoppingItem {
        val group = groupRepository.findByIdOrNull(groupId) ?: throw GroupException.NotFound.from(groupId)
        val owner =
            groupRepository.findUserById(groupId, itemDto.ownerId) ?: throw UserException.NotFound.from(itemDto.ownerId)

        val item = ShoppingItem(
            owner = owner,
            appGroup = group,
            name = itemDto.name,
            amount = itemDto.amount,
            price = itemDto.price,
            priority = itemDto.priority,
            // TODO: decide if I want to keep the data from the dto (probably yes?)
            checkingUser = null,
            checkoffTimestamp = null,
        )

        group.addShoppingItem(item)
        owner.addShoppingItem(item)

        return item
    }

    /**
     * Creates and attaches a [ExpensePart] to an [Expense].
     */
    internal fun createExpensePart(expensePartDto: ExpensePartDto, expense: Expense): ExpensePart {
        val userPart = userRepository.findByIdOrNull(expensePartDto.userId) ?: throw UserException.NotFound.from(
            expensePartDto.userId
        )

        checkUserInGroup(expense.group.id, userPart.id)

        val expensePart = ExpensePart(
            partAmount = expensePartDto.partAmount,
            partOf = expense,
            userPart = userPart,
        )

        expense.addExpensePart(expensePart)

        return expensePart
    }

    /**
     * Creates an [Expense] with its dependencies
     */
    internal fun createExpense(groupId: Long, expenseDto: ExpenseDto): Expense {
        val group = groupRepository.findByIdOrNull(groupId) ?: throw GroupException.NotFound.from(groupId)
        val owner = groupRepository.findUserById(groupId, expenseDto.ownerId) ?: throw UserException.NotFound.from(
            expenseDto.ownerId
        )
        val payer = groupRepository.findUserById(groupId, expenseDto.payerId) ?: throw UserException.NotFound.from(
            expenseDto.payerId
        )

        checkUsersInGroup(groupId, listOf(owner.id, payer.id))

        // Check that there are no users duplicated
        val duplicate =
            expenseDto.expenseParts.groupingBy { it.userId }.eachCount().entries.firstOrNull { it.value != 1 }
        if (duplicate != null)
            throw UserException.DuplicateId.from(duplicate.key)

        val expense = Expense(
            owner = owner,
            group = group,
            payer = payer,
            title = expenseDto.title,
            amount = expenseDto.amount,
            category = expenseDto.category,
            description = expenseDto.description,
        )

        // Create and attach all the parts
        for (part in expenseDto.expenseParts) {
            createExpensePart(part, expense)
        }

        group.addExpense(expense)
        owner.addOwnedExpense(expense)
        payer.addPayedExpense(expense)

        return expense
    }

    override fun getAll(pageable: Pageable): Page<GroupDto> =
        groupRepository.findAll(pageable).map { it.toDto() }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#id)")
    override fun getById(id: Long): GroupDto =
        groupRepository.findByIdOrNull(id)?.toDto() ?: throw GroupException.NotFound.from(id)

    override fun save(dto: GroupDto): GroupDto = groupRepository.save(createGroup(dto)).toDto()

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#id)")
    override fun update(
        id: Long,
        dto: GroupDto
    ): GroupDto {
        val group = createGroup(dto)
        // Create new entity if it does not exist
        group.id = if (groupRepository.existsById(id)) id else 0

        return groupRepository.save(group).toDto()
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#id)")
    override fun delete(id: Long) {
        groupRepository.deleteById(id)
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun addUser(
        groupId: Long,
        userId: Long
    ): GroupDto {
        val group = groupRepository.findByIdOrNull(groupId) ?: throw GroupException.NotFound.from(groupId)
        val user = userRepository.findByIdOrNull(userId) ?: throw UserException.NotFound.from(userId)

        group.addUser(user)

        return groupRepository.save(group).toDto()
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun removeUser(groupId: Long, userId: Long): GroupDto {
        val group = groupRepository.findByIdOrNull(groupId) ?: throw GroupException.NotFound.from(groupId)

        group.removeUser(userId)

        return groupRepository.save(group).toDto()
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun addShoppingItem(groupId: Long, item: ShoppingItemDto): ShoppingItemDto =
        shoppingItemRepository.save(createShoppingItem(groupId, item)).toDto()

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun getShoppingItems(groupId: Long, pageable: Pageable): Page<ShoppingItemDto> =
        shoppingItemRepository
            .findAllByGroupId(groupId, pageable).map { it.toDto() }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun checkShoppingItem(groupId: Long, shoppingItemId: Long, dto: CheckDto): CheckDto {
        val shoppingItem = shoppingItemRepository.findByIdAndAppGroupId(shoppingItemId, groupId)
            ?: throw ShoppingItemException.NotFound.from(shoppingItemId)

        val user =
            userRepository.findByIdOrNull(dto.checkingUserId) ?: throw UserException.NotFound.from(dto.checkingUserId)

        checkUserInGroup(groupId, user.id)

        shoppingItem.check(user, dto.checkoffTimestamp)

        return shoppingItemRepository.save(shoppingItem)
            .let { CheckDto(it.checkingUser!!.id, it.checkoffTimestamp!!) }
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun uncheckShoppingItem(groupId: Long, shoppingItemId: Long) {
        val shoppingItem = shoppingItemRepository.findByIdAndAppGroupId(shoppingItemId, groupId) ?: return

        shoppingItem.uncheck()
        shoppingItemRepository.save(shoppingItem)
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun addExpense(
        groupId: Long,
        expense: ExpenseDto
    ): ExpenseDto = expenseRepository.save(createExpense(groupId, expense)).toDto()

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun getExpenses(
        groupId: Long,
        pageable: Pageable
    ): Page<ExpenseDto> = expenseRepository.findAllByGroupId(groupId, pageable).map { it.toDto() }
}
