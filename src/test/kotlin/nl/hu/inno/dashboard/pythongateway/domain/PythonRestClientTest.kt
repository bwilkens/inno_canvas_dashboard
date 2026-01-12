package nl.hu.inno.dashboard.pythongateway.domain

import nl.hu.inno.dashboard.exception.exceptions.PythonGatewayException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.ArgumentMatchers.isNull
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

class PythonRestClientTest {
    private lateinit var restTemplate: RestTemplate
    private lateinit var client: PythonRestClient

    private val envTwoUrl = "http://env-two"
    private val envThreeUrl = "http://env-three"

    @BeforeEach
    fun setUp() {
        restTemplate = mock(RestTemplate::class.java)
        client = PythonRestClient(envTwoUrl, envThreeUrl, restTemplate)
    }

    @Test
    fun postToPythonEnvironment_returnsOk_whenEnvTwoAndResponseOk() {
        val response = ResponseEntity<Void>(HttpStatus.OK)
        `when`(restTemplate.postForEntity(eq(envTwoUrl), isNull(), eq(Void::class.java))).thenReturn(response)

        client.postToPythonEnvironment(PythonEnvironment.ENV_TWO)
        verify(restTemplate).postForEntity(eq(envTwoUrl), isNull(), eq(Void::class.java))
    }

    @Test
    fun postToPythonEnvironment_returnsOk_whenEnvThreeAndResponseOk() {
        val response = ResponseEntity<Void>(HttpStatus.OK)
        `when`(restTemplate.postForEntity(eq(envThreeUrl), isNull(), eq(Void::class.java))).thenReturn(response)

        client.postToPythonEnvironment(PythonEnvironment.ENV_THREE)
        verify(restTemplate).postForEntity(eq(envThreeUrl), isNull(), eq(Void::class.java))
    }

    @Test
    fun postToPythonEnvironment_throwsBadRequest_whenResponseBadRequest() {
        val response = ResponseEntity<Void>(HttpStatus.BAD_REQUEST)
        `when`(restTemplate.postForEntity(eq(envTwoUrl), isNull(), eq(Void::class.java))).thenReturn(response)

        val exception = assertThrows(PythonGatewayException::class.java) {
            client.postToPythonEnvironment(PythonEnvironment.ENV_TWO)
        }
        assertEquals("Bad request to Python environment", exception.message)
    }

    @Test
    fun postToPythonEnvironment_throwsInternalServerError_whenResponseInternalServerError() {
        val response = ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
        `when`(restTemplate.postForEntity(eq(envThreeUrl), isNull(), eq(Void::class.java))).thenReturn(response)

        val exception = assertThrows(PythonGatewayException::class.java) {
            client.postToPythonEnvironment(PythonEnvironment.ENV_THREE)
        }
        assertEquals("Python environment error", exception.message)
    }

    @Test
    fun postToPythonEnvironment_throwsUnexpectedResponse_whenResponseOtherStatus() {
        val response = ResponseEntity<Void>(HttpStatus.NOT_FOUND)
        `when`(restTemplate.postForEntity(eq(envTwoUrl), isNull(), eq(Void::class.java))).thenReturn(response)

        val exception = assertThrows(PythonGatewayException::class.java) {
            client.postToPythonEnvironment(PythonEnvironment.ENV_TWO)
        }
        assertEquals("Unexpected response: 404 NOT_FOUND", exception.message)
    }
}