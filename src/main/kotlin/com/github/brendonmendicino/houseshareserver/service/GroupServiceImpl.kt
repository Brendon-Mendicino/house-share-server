package com.github.brendonmendicino.houseshareserver.service

import com.github.brendonmendicino.houseshareserver.dto.*
import com.github.brendonmendicino.houseshareserver.entity.*
import com.github.brendonmendicino.houseshareserver.exception.*
import com.github.brendonmendicino.houseshareserver.mapper.toDto
import com.github.brendonmendicino.houseshareserver.repository.*
import io.micrometer.observation.annotation.Observed
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI

@Service
@Transactional
class GroupServiceImpl(
    private val groupRepository: GroupRepository,
    private val shoppingItemRepository: ShoppingItemRepository,
    private val expenseRepository: ExpenseRepository,
    private val userRepository: UserRepository,
    private val groupMemberRepository: GroupMemberRepository,
) : GroupService {
    companion object {
        private val logger = LoggerFactory.getLogger(GroupServiceImpl::class.java)
    }

    private fun getGroup(groupId: Long): AppGroup {
        return groupRepository.findByIdOrNull(groupId) ?: throw GroupException.NotFound.from(groupId)
    }

    private fun getUser(userId: Long): AppUser {
        return userRepository.findByIdOrNull(userId) ?: throw UserException.NotFound.from(userId)
    }

    private fun getUserInGroup(groupId: Long, userId: Long): AppUser {
        return userRepository.findByIdAndGroups_Id(userId, groupId)
            ?: throw UserException.NotFound.from(userId)
    }

    private fun getMemberInGroup(groupId: Long, memberId: Long): GroupMember {
        return groupMemberRepository.findByIdAndGroupId(memberId, groupId)
            ?: throw GroupMemberException.NotFound.from(memberId)
    }

    private fun addUserInternal(group: AppGroup, user: AppUser) {
        group.addUser(user)

        val member = user.toMember(group)

        group.addMember(member)
    }

    /**
     * Creates a [AppGroup] with its users.
     */
    private fun createGroup(dto: AppGroupDto): AppGroup {
        val group = AppGroup(
            name = dto.name,
            description = dto.description,
            imageUrl = dto.imageUrl,
        )

        val users = dto
            .userIds
            .map { getUser(it) }

        for (user in users) {
            addUserInternal(group, user)
        }

        return group
    }

    internal fun createMember(groupId: Long, memberDto: GroupMemberDto): GroupMember {
        val group = groupRepository.findByIdOrNull(groupId) ?: throw GroupException.NotFound.from(groupId)
        val user = if (memberDto.userId != null) {
            getUserInGroup(groupId, memberDto.userId)
        } else {
            null
        }

        val member = GroupMember(
            firstName = memberDto.firstName,
            lastName = memberDto.lastName,
            picture = memberDto.picture?.let { URI(it) },
            group = group,
            user = user
        )
        user?.members?.add(member)

        group.addMember(member)

        return member
    }

    /**
     * Creates [ShoppingItem] with its dependencies
     */
    private fun createShoppingItem(groupId: Long, itemDto: ShoppingItemDto): ShoppingItem {
        val group = groupRepository.findByIdOrNull(groupId) ?: throw GroupException.NotFound.from(groupId)
        val owner = getMemberInGroup(groupId, itemDto.ownerId)

        val item = ShoppingItem(
            owner = owner,
            group = group,
            name = itemDto.name,
            amount = itemDto.amount,
            price = itemDto.price,
            priority = itemDto.priority,
            // TODO: decide if I want to keep the data from the dto (probably yes?)
            checkingMember = null,
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
        val memberPart = getMemberInGroup(expense.group.id, expensePartDto.memberId)

        val expensePart = ExpensePart(
            partAmount = expensePartDto.partAmount,
            partOf = expense,
            memberPart = memberPart,
        )

        expense.addExpensePart(expensePart)

        return expensePart
    }

    /**
     * Creates an [Expense] with its dependencies
     */
    internal fun createExpense(groupId: Long, expenseDto: ExpenseDto): Expense {
        val group = groupRepository.findByIdOrNull(groupId) ?: throw GroupException.NotFound.from(groupId)
        val owner = getMemberInGroup(groupId, expenseDto.ownerId)
        val payer = getMemberInGroup(groupId, expenseDto.payerId)

        // Check that there are no members duplicated
        val duplicate =
            expenseDto.expenseParts.groupingBy { it.memberId }.eachCount().entries.firstOrNull { it.value != 1 }
        if (duplicate != null)
            throw UserException.DuplicateId.from(duplicate.key)

        val expense = Expense(
            owner = owner,
            group = group,
            payer = payer,
            title = expenseDto.title,
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

    @PreAuthorize("hasRole('admin')")
    override fun getAll(pageable: Pageable): Page<AppGroupDto> =
        groupRepository.findAll(pageable).map { it.toDto() }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#id)")
    override fun getById(id: Long): AppGroupDto =
        groupRepository.findByIdOrNull(id)?.toDto() ?: throw GroupException.NotFound.from(id)

    @Observed(name = "group.create")
    override fun save(dto: AppGroupDto): AppGroupDto =
        groupRepository.save(createGroup(dto)).toDto()
            .also { logger.info("Created Group@${it.id}") }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#id)")
    override fun update(
        id: Long,
        dto: AppGroupDto
    ): AppGroupDto {
        val group = groupRepository.findByIdOrNull(id) ?: return save(dto)

        group.name = dto.name
        group.description = dto.description
        group.imageUrl = dto.imageUrl

        return groupRepository.save(group)
            .also { logger.info("update: Updated {}", it.ref()) }
            .toDto()
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#id)")
    override fun delete(id: Long) {
        groupRepository.deleteById(id)
        logger.info("Deleted Group@${id}")
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun addUser(
        groupId: Long,
        userId: Long
    ): AppGroupDto {
        val group = getGroup(groupId)
        val user = getUser(userId)

        addUserInternal(group, user)

        return groupRepository.save(group)
            .also { logger.info("addUser: Added {} to {}", user.ref(), it.ref()) }
            .toDto()
    }

    /**
     * Same as [addUser], but without the authorization checks.
     */
    override fun addUserNoMember(groupId: Long, userId: Long): AppGroupDto {
        val group = getGroup(groupId)
        val user = getUser(userId)

        addUserInternal(group, user)

        return groupRepository.save(group)
            .also { logger.info("Added {} from system to {}", user.ref(), it.ref()) }
            .toDto()
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun removeUser(groupId: Long, userId: Long): AppGroupDto {
        val group = getGroup(groupId)
        val user = getUser(userId)

        val member = groupMemberRepository.findByUserAndGroup(user, group)
        if (member != null) {
            member.user = null
            groupMemberRepository.save(member)
        }

        group.removeUser(user)

        return groupRepository.save(group).toDto()
            .also { logger.info("Removed User@$userId from Group@$groupId") }
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun getUsers(groupId: Long): List<AppUserDto> {
        val group = groupRepository.findByIdOrNull(groupId) ?: throw GroupException.NotFound.from(groupId)
        return group.users.map { it.toDto() }
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun getUserById(groupId: Long, userId: Long): AppUserDto {
        return groupRepository.findUserById(groupId, userId)?.toDto() ?: throw UserException.NotFound.from(userId)
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun getMembers(groupId: Long): List<GroupMemberDto> {
        val group = getGroup(groupId)
        return group.members.map { it.toDto() }
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun getMember(groupId: Long, memberId: Long): GroupMemberDto {
        return groupMemberRepository.findByIdAndGroupId(memberId, groupId)
            ?.toDto()
            ?: throw GroupMemberException.NotFound.from(memberId)
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun addMember(groupId: Long, member: GroupMemberDto): GroupMemberDto {
        val entity = createMember(groupId, member)
        return groupMemberRepository.save(entity)
            .also { logger.info("Added {} to {}", it.ref(), it.group.ref()) }
            .toDto()
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun updateMember(groupId: Long, memberId: Long, member: GroupMemberDto): GroupMemberDto {
        val entity = getMemberInGroup(groupId, memberId)
        val user = if (member.userId != null) {
            getUserInGroup(groupId, member.userId)
        } else {
            null
        }

        entity.update(dto = member, user = user)

        return groupMemberRepository.save(entity)
            .also { logger.info("Updated {} of {}", it.ref(), it.group.ref()) }
            .toDto()
    }

    @Observed(name = "group.item.create")
    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun addShoppingItem(groupId: Long, item: ShoppingItemDto): ShoppingItemDto =
        shoppingItemRepository.save(createShoppingItem(groupId, item)).toDto()
            .also { logger.info("Added ShoppingItem@${it.id} to Group@$groupId") }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun getShoppingItems(groupId: Long, pageable: Pageable): Page<ShoppingItemDto> =
        shoppingItemRepository
            .findAllByGroupId(groupId, pageable).map { it.toDto() }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun updateShoppingItem(groupId: Long, shoppingItemId: Long, item: ShoppingItemDto): ShoppingItemDto {
        val entity = shoppingItemRepository.findByIdAndGroupId(shoppingItemId, groupId)
            ?: throw ShoppingItemException.NotFound.from(shoppingItemId)

        entity.update(item)

        return shoppingItemRepository.save(entity).toDto()
            .also { logger.info("Updated ShoppingItem@${it.id} from Group@$groupId") }
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun removeShoppingItem(groupId: Long, shoppingItemId: Long) {
        shoppingItemRepository.deleteByIdAndGroupId(shoppingItemId, groupId)
        logger.info("Deleted ShoppingItem@${shoppingItemId}")
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun checkShoppingItem(groupId: Long, shoppingItemId: Long, dto: CheckDto): CheckDto {
        val shoppingItem = shoppingItemRepository.findByIdAndGroupId(shoppingItemId, groupId)
            ?: throw ShoppingItemException.NotFound.from(shoppingItemId)

        val member = getMemberInGroup(groupId, dto.checkingMemberId)

        shoppingItem.check(member, dto.checkoffTimestamp)

        return shoppingItemRepository.save(shoppingItem)
            .also { logger.info("Checked {}", it.ref()) }
            .let { CheckDto(it.checkingMember!!.id, it.checkoffTimestamp!!) }
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun uncheckShoppingItem(groupId: Long, shoppingItemId: Long) {
        val shoppingItem = shoppingItemRepository.findByIdAndGroupId(shoppingItemId, groupId) ?: return

        shoppingItem.uncheck()
        shoppingItemRepository.save(shoppingItem)
            .also { logger.info("Unchecked ShoppingItem@$shoppingItemId") }
    }

    @Observed(name = "group.expense.create")
    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun addExpense(
        groupId: Long,
        expense: ExpenseDto
    ): ExpenseDto = expenseRepository.save(createExpense(groupId, expense)).toDto()
        .also { logger.info("Added Expense@${it.id} to Group@$groupId") }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun getExpenses(
        groupId: Long,
        pageable: Pageable
    ): Page<ExpenseDto> = expenseRepository.findAllByGroupId(groupId, pageable).map { it.toDto() }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun updateExpense(groupId: Long, expenseId: Long, dto: ExpenseDto): ExpenseDto {
        val entity =
            expenseRepository.findByIdAndGroupId(expenseId, groupId) ?: throw ExpenseException.NotFound.from(expenseId)

        entity.category = dto.category
        entity.title = dto.title
        entity.description = dto.description
        entity.owner = getMemberInGroup(groupId, dto.ownerId)
        entity.payer = getMemberInGroup(groupId, dto.payerId)

        entity.expenseParts.clear()
        for (part in dto.expenseParts) {
            createExpensePart(part, entity)
        }

        return expenseRepository.save(entity).toDto()
            .also { logger.info("Updated Expense@${it.id} from Group@$groupId") }
    }

    @PreAuthorize("hasRole('admin') || @authorizationService.isMemberOf(#groupId)")
    override fun deleteExpense(groupId: Long, expenseId: Long) {
        expenseRepository.deleteByIdAndGroupId(expenseId, groupId)
        logger.info("Deleted Expense@$expenseId in Group@$groupId")
    }
}
