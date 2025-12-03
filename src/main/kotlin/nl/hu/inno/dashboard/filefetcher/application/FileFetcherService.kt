package nl.hu.inno.dashboard.filefetcher.application

import org.springframework.core.io.Resource

interface FileFetcherService {
    fun fetchCsvFile(): Resource
    fun fetchDashboardHtml(instanceName: String, fullPath: String, role : String, email: String): Resource
}