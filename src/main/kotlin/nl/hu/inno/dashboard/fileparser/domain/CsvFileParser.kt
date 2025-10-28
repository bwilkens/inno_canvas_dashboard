package nl.hu.inno.dashboard.fileparser.domain

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.InputStreamReader

@Component
class CsvFileParser : FileParser {
    override fun supports(file: MultipartFile): Boolean {
        return file.originalFilename?.endsWith(".csv", true) == true
    }

    override fun parse(file: MultipartFile): List<List<String>> {
        val result = mutableListOf<List<String>>()
        InputStreamReader(file.inputStream).use { reader ->
            CSVParser.parse(reader, CSVFormat.DEFAULT).use { csvParser ->
                for (record in csvParser) {
                    result.add(record.map { it.trim() }.toList())
                }
            }
        }
        return result
    }
}