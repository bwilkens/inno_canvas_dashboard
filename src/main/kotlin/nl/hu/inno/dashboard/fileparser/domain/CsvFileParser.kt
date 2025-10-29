package nl.hu.inno.dashboard.fileparser.domain

import nl.hu.inno.dashboard.fileparser.domain.exception.CsvFileCannotBeReadException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.io.InputStreamReader

@Component
class CsvFileParser : FileParser {
    override fun supports(file: MultipartFile): Boolean {
        return file.originalFilename?.endsWith(suffix = ".csv", ignoreCase = true) == true
    }

    override fun parse(file: MultipartFile): List<List<String>> {
        if (file.isEmpty) {
            throw CsvFileCannotBeReadException("File is empty")
        }

        try {
            file.inputStream.use { inputStream ->
                InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                    val format = CSVFormat.DEFAULT
                        .builder()
                        .setTrim(true)
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .get()
                    val parser = CSVParser.parse(reader, format)
                    return parser.records.map { it.toList() }
                }
            }

        } catch (e: IOException) {
            throw CsvFileCannotBeReadException("File cannot be read")
        }
    }
}