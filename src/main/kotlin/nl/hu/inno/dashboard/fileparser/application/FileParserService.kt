package nl.hu.inno.dashboard.fileparser.application

import nl.hu.inno.dashboard.fileparser.domain.FileParser
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
        return TODO("Provide the return value")
    }
}