package nl.hu.inno.dashboard.fileparser.application

import org.springframework.core.io.Resource

interface FileParserService {
    fun parseFile(resource: Resource): List<List<String>>
}