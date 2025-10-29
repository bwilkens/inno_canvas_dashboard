package nl.hu.inno.dashboard.fileparser.application

import nl.hu.inno.dashboard.Fixture
import nl.hu.inno.dashboard.fileparser.domain.CsvFileParser
import nl.hu.inno.dashboard.fileparser.domain.exception.FileTypeNotSupportedException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile

class FileParserServiceImplTest {

    private val csvParser = CsvFileParser()
    private val service = FileParserServiceImpl(listOf(csvParser))

    @Test
    fun canParseValidCSV() {
        val csvContent = Fixture.fromFile("users-01.csv")
        val file = MockMultipartFile("file", "users-01.csv", "text/csv", csvContent.toByteArray())

        val actualResult = service.parseFile(file)

        val expectedResultSize = 2
        val expectedFirstRow = listOf("john.doe@student.hu.nl","john","doe","STUDENT","2600;2601")
        val firstResultRow = 0
        val expectedSecondRow = listOf("jane.doe@hu.nl","jane","doe","TEACHER","2600;2700")
        val secondResultRow = 1
        assertEquals(expectedResultSize, actualResult.size)
        assertEquals(expectedFirstRow, actualResult[firstResultRow])
        assertEquals(expectedSecondRow, actualResult[secondResultRow])
    }

    @Test
    fun unsupportedFileThrowsException() {
        val file = MockMultipartFile("file", "users-01.txt", "text/plain", "test".toByteArray())

        val actualException = assertThrows(FileTypeNotSupportedException::class.java) {
            service.parseFile(file)
        }
        val expectedMessage = "No available parser found for file: users-01.txt"
        assertEquals(expectedMessage, actualException.message)
    }
}