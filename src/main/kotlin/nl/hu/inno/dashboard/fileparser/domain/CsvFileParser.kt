package nl.hu.inno.dashboard.fileparser.domain

import nl.hu.inno.dashboard.exception.exceptions.EmptyFileException
import nl.hu.inno.dashboard.exception.exceptions.FileCannotBeReadException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.InputStreamReader

@Component
class CsvFileParser : FileParser {
    override fun supports(resource: Resource): Boolean {
        return resource.filename?.endsWith(suffix = ".csv", ignoreCase = true) == true
    }

    override fun parse(resource: Resource): List<List<String>> {
        if (!resource.exists() || resource.contentLength() == 0L) {
            throw EmptyFileException("File is empty or does not exist")
        }

        try {
            resource.inputStream.use { inputStream ->
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
            throw FileCannotBeReadException("File cannot be read")
        }
    }
}