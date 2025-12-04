package nl.hu.inno.dashboard.filefetcher.application

import org.springframework.core.io.Resource

interface FileFetcherService {
    fun fetchCsvFile(): Resource
    fun fetchDashboardHtml(email: String, role : String, instanceName: String, relativeRequestPath: String): Resource
}