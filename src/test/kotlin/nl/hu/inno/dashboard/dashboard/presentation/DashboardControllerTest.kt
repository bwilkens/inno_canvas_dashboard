package nl.hu.inno.dashboard.dashboard.presentation

import nl.hu.inno.dashboard.dashboard.application.DashboardServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.ResponseEntity

class DashboardControllerTest {
    private lateinit var service: DashboardServiceImpl
    private lateinit var controller: DashboardController

    @BeforeEach
    fun setUp() {
        service = mock(DashboardServiceImpl::class.java)
        controller = DashboardController(service)
    }

    @Test
    fun addCourse_callsServiceAndReturnsOk() {
        val response = controller.addUsersAndCourses()

        verify(service).addUsersToCourse()
        assertEquals(ResponseEntity.ok().build<Void>(), response)
    }

    @Test
    fun addCourse_handlesException() {
        doThrow(RuntimeException("fail")).`when`(service).addUsersToCourse()
        val response = controller.addUsersAndCourses()

        assertEquals(ResponseEntity.internalServerError().build<Void>(), response)
    }

    @Test
    fun updateCourse_callsServiceAndReturnsOk() {
        val response = controller.updateUsersAndCourses()

        verify(service).updateUsersInCourse()
        assertEquals(ResponseEntity.ok().build<Void>(), response)
    }

    @Test
    fun updateCourse_handlesException() {
        doThrow(RuntimeException("fail")).`when`(service).updateUsersInCourse()
        val response = controller.updateUsersAndCourses()

        assertEquals(ResponseEntity.internalServerError().build<Void>(), response)
    }
}