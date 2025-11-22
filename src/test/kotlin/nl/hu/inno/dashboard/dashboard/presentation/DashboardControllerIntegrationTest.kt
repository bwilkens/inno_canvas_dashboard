package nl.hu.inno.dashboard.dashboard.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import nl.hu.inno.dashboard.dashboard.application.DashboardServiceImpl
import nl.hu.inno.dashboard.dashboard.application.dto.UsersDTO
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(DashboardController::class)
class DashboardControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var service: DashboardServiceImpl

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun getCurrentUser_returnsUser_whenEmailPresentAndUserFound() {
        val userDTO = UsersDTO(email = "john.doe@student.hu.nl", name = "John Doe", role = "STUDENT")
        `when`(service.findUserByEmail("john.doe@student.hu.nl")).thenReturn(userDTO)

        mockMvc.perform(
            get("/api/v1/dashboard/users/")
                .with(
                    oauth2Login().attributes { it["email"] = "john.doe@student.hu.nl" }
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
    }

    @Test
    fun getCurrentUser_returnsNotFound_whenEmailMissing() {
        mockMvc.perform(
            get("/api/v1/dashboard/users/")
                .with(oauth2Login().attributes { })
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun getCurrentUser_returnsNotFound_whenUserNotFoundInDatabase() {
        `when`(service.findUserByEmail("john.doe@student.hu.nl")).thenReturn(null)

        mockMvc.perform(
            get("/api/v1/dashboard/users/")
                .with(
                    oauth2Login().attributes { it["email"] = "john.doe@student.hu.nl" }
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun addCourse_returnsOk() {
        mockMvc.perform(
            post("/api/v1/dashboard/internal/users/new")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user("testuser"))
        ).andExpect(status().isOk)

        verify(service).addUsersToCourse()
    }

    @Test
    fun addCourse_handlesException() {
        doThrow(RuntimeException("fail")).`when`(service).addUsersToCourse()

        mockMvc.perform(
            post("/api/v1/dashboard/internal/users/new")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user("testuser"))
        ).andExpect(status().isInternalServerError)

        verify(service).addUsersToCourse()
    }

    @Test
    fun updateCourse_returnsOk() {
        mockMvc.perform(
            post("/api/v1/dashboard/internal/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user("testuser"))
        ).andExpect(status().isOk)

        verify(service).updateUsersInCourse()
    }

    @Test
    fun updateCourse_handlesException() {
        doThrow(RuntimeException("fail")).`when`(service).updateUsersInCourse()

        mockMvc.perform(
            post("/api/v1/dashboard/internal/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user("testuser"))
        ).andExpect(status().isInternalServerError)

        verify(service).updateUsersInCourse()
    }
}