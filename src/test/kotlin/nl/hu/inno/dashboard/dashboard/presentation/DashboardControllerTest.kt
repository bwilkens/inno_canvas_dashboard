package nl.hu.inno.dashboard.dashboard.presentation

import jakarta.servlet.http.HttpServletRequest
import nl.hu.inno.dashboard.dashboard.application.DashboardServiceImpl
import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
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

class DashboardControllerTest {
    private lateinit var service: DashboardServiceImpl
    private lateinit var controller: DashboardController

    @BeforeEach
    fun setUp() {
        service = mock(DashboardServiceImpl::class.java)
        controller = DashboardController(service)
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
    fun refreshUsersAndCourses_callsServiceAndReturnsOk() {
        val actualResponse = controller.refreshUsersAndCourses()

        verify(service).refreshUsersAndCourses()
        assertEquals(ResponseEntity.ok().build<Void>(), actualResponse)
    }

    @Test
    fun refreshUsersAndCourses_handlesException() {
        doThrow(RuntimeException("fail")).`when`(service).refreshUsersAndCourses()
        assertThrows<RuntimeException> {
            controller.refreshUsersAndCourses()
        }
    }
}