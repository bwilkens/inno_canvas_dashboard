package nl.hu.inno.dashboard.dashboard.data

import nl.hu.inno.dashboard.dashboard.domain.Privilege
import nl.hu.inno.dashboard.dashboard.domain.Users
import org.springframework.data.jpa.repository.JpaRepository

interface UsersRepository : JpaRepository<Users, String> {
    fun findAllByEmailEndingWith(emailSuffix: String): List<Users>
    fun deleteAllByPrivilege(privilege: Privilege)
}