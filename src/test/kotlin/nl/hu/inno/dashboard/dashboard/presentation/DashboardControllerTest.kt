package nl.hu.inno.dashboard.dashboard.presentation

import nl.hu.inno.dashboard.dashboard.application.DashboardServiceImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile

class DashboardControllerTest {
    @Test
    fun updateUsersInCourse_callsServiceAndReturnsOk() {
        val service = mock(DashboardServiceImpl::class.java)
        val controller = DashboardController(service)
        val file = mock(MultipartFile::class.java)

        val response = controller.updateUsersInCourse(file)

        verify(service).updateUsersInCourse(file)
        assertEquals(ResponseEntity.ok().build<Void>(), response)
    }

    @Test
    fun updateUsersInCourse_handlesException() {
        val service = mock(DashboardServiceImpl::class.java)
        val controller = DashboardController(service)
        val file = mock(MultipartFile::class.java)

        doThrow(RuntimeException("fail")).`when`(service).updateUsersInCourse(file)
        val response = controller.updateUsersInCourse(file)

        assertEquals(ResponseEntity.internalServerError().build<Void>(), response)
    }

    @Test
    fun replaceUsersInCourse_callsServiceAndReturnsOk() {
        val service = mock(DashboardServiceImpl::class.java)
        val controller = DashboardController(service)
        val file = mock(MultipartFile::class.java)

        val response = controller.replaceUsersInCourse(file)

        verify(service).replaceUsersInCourse(file)
        assertEquals(ResponseEntity.ok().build<Void>(), response)
    }

    @Test
    fun replaceUsersInCourse_handlesException() {
        val service = mock(DashboardServiceImpl::class.java)
        val controller = DashboardController(service)
        val file = mock(MultipartFile::class.java)

        doThrow(RuntimeException("fail")).`when`(service).replaceUsersInCourse(file)
        val response = controller.replaceUsersInCourse(file)

        assertEquals(ResponseEntity.internalServerError().build<Void>(), response)
    }
}