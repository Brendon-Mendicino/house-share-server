package com.github.brendonmendicino.houseshareserver.entity

import com.github.brendonmendicino.houseshareserver.validator.NotBlankIfPresent
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import org.hibernate.annotations.CreationTimestamp
import java.net.URI
import java.time.OffsetDateTime

@Entity
@Table(
    indexes = [
        Index(columnList = "sub")
    ]
)
class AppUser(
    @NotBlankIfPresent
    @Column(nullable = false)
    var username: String,

    @Email
    var email: String?,

    @NotBlankIfPresent
    var firstName: String?,

    @NotBlankIfPresent
    var lastName: String?,
    /**
     * OAuth2 User ID. Specify if this user was created using
     * the OAuth2 authentication flow.
     */
    @Column(unique = true)
    var sub: String?,

    @Column(columnDefinition = "TEXT")
    var picture: URI?,
) : BaseEntity() {
    @CreationTimestamp
    lateinit var createdAt: OffsetDateTime

    @ManyToMany(mappedBy = "users")
    var groups: MutableList<AppGroup> = mutableListOf()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.MERGE])
    var members: MutableList<GroupMember> = mutableListOf()

    fun toMember(group: AppGroup) = GroupMember(
        firstName = firstName ?: username,
        lastName = lastName,
        picture = picture,
        group = group,
        user = this,
    )
}