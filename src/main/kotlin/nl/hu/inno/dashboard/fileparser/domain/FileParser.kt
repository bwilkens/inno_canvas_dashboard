package nl.hu.inno.dashboard.fileparser.domain

import org.springframework.core.io.Resource

interface FileParser {
    fun supports(resource: Resource): Boolean
    fun parse(resource: Resource): List<List<String>>
}