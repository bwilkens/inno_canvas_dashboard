package nl.hu.inno.dashboard.dashboard.presentation

import nl.hu.inno.dashboard.dashboard.application.DashboardServiceImpl
import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.core.user.OAuth2User

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
        val expectedMockUserDTO = mock(UsersDTO::class.java)
        `when`(service.findUserByEmail("john.doe@student.hu.nl")).thenReturn(expectedMockUserDTO)

        val actualResponse = controller.getCurrentUser(mockUser)

        assertEquals(ResponseEntity.ok(expectedMockUserDTO), actualResponse)
    }

    @Test
    fun getCurrentUser_returnsNotFound_whenEmailMissing() {
        val mockUser = mock(OAuth2User::class.java)
        `when`(mockUser.attributes).thenReturn(emptyMap<String, Any>())

        val actualResponse = controller.getCurrentUser(mockUser)

        assertEquals(ResponseEntity.notFound().build<UsersDTO>(), actualResponse)
    }

    @Test
    fun getCurrentUser_returnsNotFound_whenUserNotFoundInDatabase() {
        val user = mock(OAuth2User::class.java)
        `when`(user.attributes).thenReturn(mapOf("email" to "john.doe@student.hu.nl"))
        `when`(service.findUserByEmail("john.doe@student.hu.nl")).thenReturn(null)

        val actualResponse = controller.getCurrentUser(user)

        assertEquals(ResponseEntity.notFound().build<UsersDTO>(), actualResponse)
    }

    @Test
    fun addCourse_callsServiceAndReturnsOk() {
        val actualResponse = controller.addUsersAndCourses()

        verify(service).addUsersToCourse()
        assertEquals(ResponseEntity.ok().build<Void>(), actualResponse)
    }

    @Test
    fun addCourse_handlesException() {
        doThrow(RuntimeException("fail")).`when`(service).addUsersToCourse()
        val actualResponse = controller.addUsersAndCourses()

        assertEquals(ResponseEntity.internalServerError().build<Void>(), actualResponse)
    }

    @Test
    fun updateCourse_callsServiceAndReturnsOk() {
        val actualResponse = controller.updateUsersAndCourses()

        verify(service).updateUsersInCourse()
        assertEquals(ResponseEntity.ok().build<Void>(), actualResponse)
    }

    @Test
    fun updateCourse_handlesException() {
        doThrow(RuntimeException("fail")).`when`(service).updateUsersInCourse()
        val actualResponse = controller.updateUsersAndCourses()

        assertEquals(ResponseEntity.internalServerError().build<Void>(), actualResponse)
    }
}