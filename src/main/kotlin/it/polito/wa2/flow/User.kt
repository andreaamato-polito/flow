package it.polito.wa2.flow

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.Date

@Table("users")
data class User(
    @Id
    val id: Int,
    val name: String,
    val pass: String,
    val email: String,
    val createdOn: LocalDateTime,
    val lastAccess: LocalDateTime?
)