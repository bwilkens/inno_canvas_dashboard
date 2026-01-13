package nl.hu.inno.dashboard.pythongateway.application

import nl.hu.inno.dashboard.dashboard.application.DashboardService
import nl.hu.inno.dashboard.exception.exceptions.InvalidPythonEnvironmentException
import nl.hu.inno.dashboard.pythongateway.domain.PythonEnvironment
import nl.hu.inno.dashboard.pythongateway.domain.PythonRestClient
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class PythonGatewayServiceImpl(
    private val dashboardService: DashboardService,
    private val pythonRestClient: PythonRestClient
) : PythonGatewayService {

    override fun startPythonScript(email: String, environment: String) {
        log.info("startPythonScript requested: email={}, environment={}", email, environment)
        dashboardService.verifyUserIsAdminOrSuperAdmin(email)

        val requestedEnvironment = parsePythonEnvironment(environment)
        log.info("Posting to python environment: env={}, requestedBy={}", requestedEnvironment, email)
        pythonRestClient.postToPythonEnvironment(requestedEnvironment)
    }

    private fun parsePythonEnvironment(environment: String): PythonEnvironment =
        when (environment.trim().uppercase()) {
            "ENV_TWO" -> PythonEnvironment.ENV_TWO
            "ENV_THREE" -> PythonEnvironment.ENV_THREE
            else -> {
                log.warn("Invalid python environment requested: environment={}", environment)
                throw InvalidPythonEnvironmentException("Invalid Python environment: $environment")
            }
    }

    companion object {
        private val log = LoggerFactory.getLogger(PythonGatewayServiceImpl::class.java)
    }
}