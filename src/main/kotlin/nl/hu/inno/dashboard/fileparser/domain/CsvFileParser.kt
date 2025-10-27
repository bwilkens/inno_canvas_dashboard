package nl.hu.inno.dashboard.fileparser.domain

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class CsvFileParser : FileParser {
    override fun supports(file: MultipartFile): Boolean {
        return file.originalFilename?.endsWith(".csv", true) == true
    }

    override fun parse(file: MultipartFile): List<Array<String>> {
        TODO("Not yet implemented")
    }
}