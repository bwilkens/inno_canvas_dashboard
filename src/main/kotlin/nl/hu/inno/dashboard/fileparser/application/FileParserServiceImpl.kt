package nl.hu.inno.dashboard.fileparser.application

import nl.hu.inno.dashboard.exception.exceptions.FileTypeNotSupportedException
import nl.hu.inno.dashboard.fileparser.domain.FileParser
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class FileParserServiceImpl(
    private val fileParsers: List<FileParser>,
) : FileParserService {
    override fun parseFile(resource: Resource): List<List<String>> {
        val parser = selectParser(resource)
        return parser.parse(resource)
    }

    private fun selectParser(resource: Resource): FileParser {
        for (parser in fileParsers) {
            if (parser.supports(resource)) {
                return parser
            }
        }
        throw FileTypeNotSupportedException("No available parser found for file: ${resource.filename}")
    }
}