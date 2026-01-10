package nl.hu.inno.dashboard.dashboard.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import nl.hu.inno.dashboard.dashboard.application.DashboardServiceImpl
import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
import nl.hu.inno.dashboard.exception.exceptions.UserNotFoundException
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(V1DashboardController::class)
class V1DashboardControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var service: DashboardServiceImpl

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun getCurrentUser_returnsUser_whenEmailPresentAndUserFound() {
        val userDTO = UsersDTO(email = "john.doe@student.hu.nl", name = "John Doe", appRole = "STUDENT")
        `when`(service.findUserByEmail("john.doe@student.hu.nl")).thenReturn(userDTO)

        mockMvc.perform(
            get("/api/v1/dashboard/users")
                .with(
                    oauth2Login().attributes { it["email"] = "john.doe@student.hu.nl" }
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
    }

    @Test
    fun getCurrentUser_returnsUnauthorized_whenEmailMissing() {
        mockMvc.perform(
            get("/api/v1/dashboard/users")
                .with(oauth2Login().attributes { })
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun getCurrentUser_returnsNotFound_whenUserNotFoundInDatabase() {
        `when`(service.findUserByEmail("john.doe@student.hu.nl")).thenThrow(UserNotFoundException("User with email john.doe@student.hu.nl not found"))

        mockMvc.perform(
            get("/api/v1/dashboard/users")
                .with(
                    oauth2Login().attributes { it["email"] = "john.doe@student.hu.nl" }
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun getDashboard_returnsResource_whenEmailPresent() {
        val instanceName = "testInstance"
        val email = "john.doe@student.hu.nl"
        val relativeRequestPath = "$instanceName/dashboard"
        val expectedResource = ByteArrayResource("html".toByteArray())
        `when`(service.getDashboardHtml(email, instanceName, relativeRequestPath)).thenReturn(expectedResource)

        mockMvc.perform(
            get("/api/v1/dashboard/$relativeRequestPath")
                .with(oauth2Login().attributes { it["email"] = email })
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
        )
            .andExpect(status().isOk)
            .andExpect { result ->
                assert(result.response.contentAsByteArray.contentEquals("html".toByteArray()))
            }
    }

    @Test
    fun getDashboard_returnsUnauthorized_whenEmailMissing() {
        val instanceName = "testInstance"
        val relativeRequestPath = "$instanceName/dashboard"

        mockMvc.perform(
            get("/api/v1/dashboard/$relativeRequestPath")
                .with(oauth2Login().attributes { }) // geen email
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun getDashboard_returnsUnauthorized_whenEmailIsBlank() {
        val instanceName = "testInstance"
        val relativeRequestPath = "$instanceName/dashboard"

        mockMvc.perform(
            get("/api/v1/dashboard/$relativeRequestPath")
                .with(oauth2Login().attributes { it["email"] = "" })
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun refreshUsersAndCourses_returnsOk() {
        mockMvc.perform(
            post("/api/v1/dashboard/users/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(oauth2Login().attributes { it["email"] = "admin@hu.nl" })
        ).andExpect(status().isOk)

        verify(service).refreshUsersAndCoursesWithRoleCheck("admin@hu.nl")
    }

    @Test
    fun refreshUsersAndCourses_handlesException() {
        doThrow(RuntimeException("fail")).`when`(service).refreshUsersAndCoursesWithRoleCheck("admin@hu.nl")

        mockMvc.perform(
            post("/api/v1/dashboard/users/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(oauth2Login().attributes { it["email"] = "admin@hu.nl" })
        ).andExpect(status().isInternalServerError)

        verify(service).refreshUsersAndCoursesWithRoleCheck("admin@hu.nl")
    }
}