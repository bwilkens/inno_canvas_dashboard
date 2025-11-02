package nl.hu.inno.dashboard.dashboard.presentation

import nl.hu.inno.dashboard.dashboard.application.DashboardServiceImpl
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(DashboardController::class)
class DashboardControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var service: DashboardServiceImpl

    @Test
    fun canUpdateUsersInCourse() {
        val file = MockMultipartFile("file", "test.csv", "text/csv", "dummy,data".toByteArray())

        mockMvc.perform(
            multipart("/api/v1/dashboard/update-course")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(csrf())
                .with(user("testuser"))
        ).andExpect(status().isOk)
    }

    @Test
    fun canReplaceUsersInCourse() {
        val file = MockMultipartFile("file", "test.csv", "text/csv", "dummy,data".toByteArray())

        mockMvc.perform(
            multipart("/api/v1/dashboard/replace-course")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(csrf())
                .with(user("testuser"))
        ).andExpect(status().isOk)
    }
}