package nl.hu.inno.dashboard.pythongateway.presentation

import nl.hu.inno.dashboard.pythongateway.application.PythonGatewayService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.core.user.OAuth2User

class PythonGatewayControllerV1Test {
    private lateinit var service: PythonGatewayService
    private lateinit var controller: PythonGatewayControllerV1

    @BeforeEach
    fun setUp() {
        service = mock(PythonGatewayService::class.java)
        controller = PythonGatewayControllerV1(service)
    }

    @Test
    fun getDashboard_returnsOk_whenEmailPresent() {
        val mockUser = mock(OAuth2User::class.java)
        `when`(mockUser.attributes).thenReturn(mapOf("email" to "john.doe@student.hu.nl"))
        val environment = "test-env"

        val response = controller.getDashboard(environment, mockUser)

        verify(service).startPythonScript("john.doe@student.hu.nl", environment)
        assertEquals(ResponseEntity.ok().build<Void>(), response)
    }

    @Test
    fun getDashboard_returnsUnauthorized_whenEmailMissing() {
        val mockUser = mock(OAuth2User::class.java)
        `when`(mockUser.attributes).thenReturn(emptyMap<String, Any>())
        val environment = "test-env"

        val response = controller.getDashboard(environment, mockUser)

        verify(service, never()).startPythonScript(anyString(), anyString())
        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<Void>(), response)
    }

    @Test
    fun getDashboard_returnsUnauthorized_whenEmailIsBlank() {
        val mockUser = mock(OAuth2User::class.java)
        `when`(mockUser.attributes).thenReturn(mapOf("email" to ""))
        val environment = "test-env"

        val response = controller.getDashboard(environment, mockUser)

        verify(service, never()).startPythonScript(anyString(), anyString())
        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<Void>(), response)
    }
}