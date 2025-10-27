package nl.hu.inno.dashboard.fileparser.application

import nl.hu.inno.dashboard.fileparser.domain.FileParser
import nl.hu.inno.dashboard.fileparser.domain.exception.FileTypeNotSupportedException
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FileParserService(
    private val fileParsers: List<FileParser>,
) {
    fun parseFile(multipartFile: MultipartFile): List<Array<String>> {
        val parser = selectParser(multipartFile)
        return parser.parse(multipartFile)
    }

    private fun selectParser(file: MultipartFile): FileParser {
        for (parser in fileParsers) {
            if (parser.supports(file)) {
                return parser
            }
        }
        throw FileTypeNotSupportedException("No available parser found for file: ${file.originalFilename}")
    }
}