package nl.hu.inno.dashboard.dashboard.presentation

import nl.hu.inno.dashboard.dashboard.application.DashboardServiceImpl
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(DashboardController::class)
class DashboardControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var service: DashboardServiceImpl

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