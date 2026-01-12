package nl.hu.inno.dashboard.pythongateway.domain

import nl.hu.inno.dashboard.exception.exceptions.PythonGatewayException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

@Component
class PythonRestClient(
    @Value("\${python.gateway.env.two.url}")
    private val pythonEnvTwoUrl: String,
    @Value("\${python.gateway.env.three.url}")
    private val pythonEnvThreeUrl: String,
    private val restTemplate: RestTemplate
) {
    fun postToPythonEnvironment(environment: PythonEnvironment) {
        val url = when (environment) {
            PythonEnvironment.ENV_TWO -> pythonEnvTwoUrl
            PythonEnvironment.ENV_THREE -> pythonEnvThreeUrl
        }
        val response: ResponseEntity<Void> = restTemplate.postForEntity<Void>(url)

        when (response.statusCode) {
            HttpStatus.OK -> {
            }
            HttpStatus.BAD_REQUEST -> {
                throw PythonGatewayException("Bad request to Python environment")
            }
            HttpStatus.INTERNAL_SERVER_ERROR -> {
                throw PythonGatewayException("Python environment error")
            }
            else -> {
                throw PythonGatewayException("Unexpected response: ${response.statusCode}")
            }
        }
    }
}