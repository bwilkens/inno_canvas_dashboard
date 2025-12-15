package nl.hu.inno.dashboard.dashboard.data

import nl.hu.inno.dashboard.dashboard.domain.AppRole
import nl.hu.inno.dashboard.dashboard.domain.Users
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UsersRepository : JpaRepository<Users, String> {
    @Query(
        "SELECT u FROM Users u WHERE u.appRole IN (:roles) OR u.email LIKE %:emailSuffix"
    )
    fun findAllAdminCandidates(roles: List<AppRole>, emailSuffix: String): List<Users>
    fun deleteAllByAppRole(appRole: AppRole)
}