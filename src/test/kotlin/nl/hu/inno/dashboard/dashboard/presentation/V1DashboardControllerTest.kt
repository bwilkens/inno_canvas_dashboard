package nl.hu.inno.dashboard.dashboard.presentation

import jakarta.servlet.http.HttpServletRequest
import nl.hu.inno.dashboard.dashboard.application.DashboardServiceImpl
import nl.hu.inno.dashboard.dashboard.application.dto.AdminDTO
import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
import nl.hu.inno.dashboard.dashboard.presentation.dto.UserPutRequest
import nl.hu.inno.dashboard.exception.exceptions.UserNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource

class V1DashboardControllerTest {
    private lateinit var service: DashboardServiceImpl
    private lateinit var controller: V1DashboardController

    @BeforeEach
    fun setUp() {
        service = mock(DashboardServiceImpl::class.java)
        controller = V1DashboardController(service)
    }

    @Test
    fun getCurrentUser_returnsUser_whenEmailPresentAndUserFound() {
        val mockUser = mock(OAuth2User::class.java)
        `when`(mockUser.attributes).thenReturn(mapOf("email" to "John.Doe@student.hu.nl"))
        val expectedUserDTO = UsersDTO(email = "john.doe@student.hu.nl", name = "John Doe", appRole = "STUDENT")
        `when`(service.findUserByEmail("John.Doe@student.hu.nl")).thenReturn(expectedUserDTO)

        val actualResponse = controller.getCurrentUser(mockUser)

        assertEquals(ResponseEntity.ok(expectedUserDTO), actualResponse)
    }

    @Test
    fun getCurrentUser_returnsUnauthorized_whenEmailMissing() {
        val mockUser = mock(OAuth2User::class.java)
        `when`(mockUser.attributes).thenReturn(emptyMap<String, Any>())

        val actualResponse = controller.getCurrentUser(mockUser)

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<UsersDTO>(), actualResponse)
    }

    @Test
    fun getCurrentUser_throwsUserNotFoundException_whenUserNotFoundInDatabase() {
        val mockUser = mock(OAuth2User::class.java)
        `when`(mockUser.attributes).thenReturn(mapOf("email" to "john.doe@student.hu.nl"))
        `when`(service.findUserByEmail("john.doe@student.hu.nl"))
            .thenThrow(UserNotFoundException("User with email john.doe@student.hu.nl not found"))

        assertThrows<UserNotFoundException> {
            controller.getCurrentUser(mockUser)
        }
    }

    @Test
    fun getCurrentUser_returnsUnauthorized_whenEmailIsBlank() {
        val mockUser = mock(OAuth2User::class.java)
        `when`(mockUser.attributes).thenReturn(mapOf("email" to ""))

        val actualResponse = controller.getCurrentUser(mockUser)

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<UsersDTO>(), actualResponse)
    }

    @Test
    fun getAllAdminUsers_returnsAdmins_whenEmailPresent() {
        val mockUser = mock(OAuth2User::class.java)
        val email = "admin@hu.nl"
        val expectedResponse = listOf(
            AdminDTO("admin@hu.nl", "Admin", "SUPERADMIN"),
            AdminDTO("other@hu.nl", "Other", "ADMIN")
        )
        `when`(mockUser.attributes).thenReturn(mapOf("email" to email))
        `when`(service.findAllAdmins(email)).thenReturn(expectedResponse)

        val actualResponse = controller.getAllAdminUsers(mockUser)

        assertEquals(ResponseEntity.ok(expectedResponse), actualResponse)
    }

    @Test
    fun getAllAdminUsers_returnsUnauthorized_whenEmailMissing() {
        val mockUser = mock(OAuth2User::class.java)
        `when`(mockUser.attributes).thenReturn(emptyMap<String, Any>())

        val actualResponse = controller.getAllAdminUsers(mockUser)

        val expectedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<List<AdminDTO>>()
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun getAllAdminUsers_returnsUnauthorized_whenEmailIsBlank() {
        val mockUser = mock(OAuth2User::class.java)
        `when`(mockUser.attributes).thenReturn(mapOf("email" to ""))

        val actualResponse = controller.getAllAdminUsers(mockUser)

        val expectedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<List<AdminDTO>>()
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun updateAdminRoles_returnsUpdatedAdmins_whenEmailPresent() {
        val mockUser = mock(OAuth2User::class.java)
        val email = "superadmin@hu.nl"
        val userPutRequests = listOf(
            UserPutRequest("admin@hu.nl", "Admin", "ADMIN"),
            UserPutRequest("other@hu.nl", "Other", "SUPERADMIN")
        )
        val expectedAdmins = listOf(
            AdminDTO("admin@hu.nl", "Admin", "ADMIN"),
            AdminDTO("other@hu.nl", "Other", "SUPERADMIN")
        )
        `when`(mockUser.attributes).thenReturn(mapOf("email" to email))
        `when`(service.updateAdminUserRoles(anyString(), anyList())).thenReturn(expectedAdmins)

        val actualResponse = controller.updateAdminRoles(mockUser, userPutRequests)

        val expectedResponse = ResponseEntity.ok(expectedAdmins)
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun updateAdminRoles_returnsUnauthorized_whenEmailMissing() {
        val mockUser = mock(OAuth2User::class.java)
        val userPutRequests = listOf(
            UserPutRequest("admin@hu.nl", "Admin", "ADMIN")
        )
        `when`(mockUser.attributes).thenReturn(emptyMap<String, Any>())

        val actualResponse = controller.updateAdminRoles(mockUser, userPutRequests)

        val expectedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<List<AdminDTO>>()
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun updateAdminRoles_returnsUnauthorized_whenEmailIsBlank() {
        val mockUser = mock(OAuth2User::class.java)
        val userPutRequests = listOf(
            UserPutRequest("admin@hu.nl", "Admin", "ADMIN")
        )
        `when`(mockUser.attributes).thenReturn(mapOf("email" to ""))

        val actualResponse = controller.updateAdminRoles(mockUser, userPutRequests)

        val expectedResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<List<AdminDTO>>()
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun getDashboard_returnsResource_whenEmailPresent() {
        val mockUser = mock(OAuth2User::class.java)
        val instanceName = "testInstance"
        val email = "john.doe@student.hu.nl"
        val request = mock(HttpServletRequest::class.java)
        val relativeRequestPath = "testInstance/dashboard"
        val expectedResource = ByteArrayResource("html".toByteArray())
        `when`(mockUser.attributes).thenReturn(mapOf("email" to email))
        `when`(request.requestURI).thenReturn("/api/v1/dashboard/$relativeRequestPath")
        `when`(service.getDashboardHtml(email, instanceName, relativeRequestPath)).thenReturn(expectedResource)

        val response = controller.getDashboard(instanceName, mockUser, request)

        assertEquals(ResponseEntity.ok(expectedResource), response)
    }

    @Test
    fun getDashboard_returnsUnauthorized_whenEmailMissing() {
        val mockUser = mock(OAuth2User::class.java)
        val instanceName = "testInstance"
        val request = mock(HttpServletRequest::class.java)
        `when`(mockUser.attributes).thenReturn(emptyMap<String, Any>())
        `when`(request.requestURI).thenReturn("/api/v1/dashboard/testInstance/dashboard")

        val response = controller.getDashboard(instanceName, mockUser, request)

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<Resource>(), response)
    }

    @Test
    fun getDashboard_returnsUnauthorized_whenEmailIsBlank() {
        val mockUser = mock(OAuth2User::class.java)
        val instanceName = "testInstance"
        val request = mock(HttpServletRequest::class.java)
        `when`(mockUser.attributes).thenReturn(mapOf("email" to ""))
        `when`(request.requestURI).thenReturn("/api/v1/dashboard/testInstance/dashboard")

        val response = controller.getDashboard(instanceName, mockUser, request)

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<Resource>(), response)
    }

    @Test
    fun refreshUsersAndCourses_returnsOk_whenEmailPresent() {
        val mockUser = mock(OAuth2User::class.java)
        val email = "admin@hu.nl"
        `when`(mockUser.attributes).thenReturn(mapOf("email" to email))

        val actualResponse = controller.refreshUsersAndCourses(mockUser)

        verify(service).refreshUsersAndCoursesWithRoleCheck(email)
        assertEquals(ResponseEntity.ok().build<Void>(), actualResponse)
    }

    @Test
    fun refreshUsersAndCourses_returnsUnauthorized_whenEmailMissing() {
        val mockUser = mock(OAuth2User::class.java)
        `when`(mockUser.attributes).thenReturn(emptyMap<String, Any>())

        val actualResponse = controller.refreshUsersAndCourses(mockUser)

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<Void>(), actualResponse)
    }

    @Test
    fun refreshUsersAndCourses_returnsUnauthorized_whenEmailIsBlank() {
        val mockUser = mock(OAuth2User::class.java)
        `when`(mockUser.attributes).thenReturn(mapOf("email" to ""))

        val actualResponse = controller.refreshUsersAndCourses(mockUser)

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<Void>(), actualResponse)
    }

    @Test
    fun refreshUsersAndCourses_throwsException_whenServiceThrows() {
        val mockUser = mock(OAuth2User::class.java)
        val email = "admin@hu.nl"
        `when`(mockUser.attributes).thenReturn(mapOf("email" to email))
        doThrow(RuntimeException("fail")).`when`(service).refreshUsersAndCoursesWithRoleCheck(email)

        assertThrows<RuntimeException> {
            controller.refreshUsersAndCourses(mockUser)
        }
    }
}