package com.github.brendonmendicino.houseshareserver.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
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
    @NotEmpty
    @Column(nullable = false)
    var username: String,

    @Email
    @NotEmpty
    var email: String?,

    @NotEmpty
    var firstName: String?,

    @NotEmpty
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

    @ManyToMany(cascade = [CascadeType.MERGE])
    var groups: MutableSet<AppGroup> = mutableSetOf()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.MERGE])
    var members: MutableList<GroupMember> = mutableListOf()
}