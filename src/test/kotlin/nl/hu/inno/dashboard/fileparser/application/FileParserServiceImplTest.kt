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
        val expectedFirstRow = listOf("50304","Innovation Semester - September 2025","2025-09-01 00:00:00+02:00","2026-01-30 23:59:59+01:00","John Doe","john.doe@student.hu.nl","STUDENT")
        val firstResultRow = 0
        val expectedSecondRow = listOf("50304","Innovation Semester - September 2025","2025-09-01 00:00:00+02:00","2026-01-30 23:59:59+01:00","Jane Doe","jane.doe@hu.nl","TEACHER")
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