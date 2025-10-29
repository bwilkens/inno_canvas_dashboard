package nl.hu.inno.dashboard.fileparser.domain

import nl.hu.inno.dashboard.Fixture
import nl.hu.inno.dashboard.fileparser.domain.exception.CsvFileCannotBeReadException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

class CsvFileParserTest {

    private val parser = CsvFileParser()

    @Test
    fun supportsValidCSV() {
        val file = MockMultipartFile("file", "users-01.csv", "text/csv", byteArrayOf())

        assertTrue(parser.supports(file))
    }

    @Test
    fun doesNotSupportInvalidCSV() {
        val file = MockMultipartFile("file", "users-01.txt", "text/plain", byteArrayOf())

        assertFalse(parser.supports(file))
    }

    @Test
    fun doesNotSupportNullFilename() {
        val mockFile = Mockito.mock(MultipartFile::class.java)

        Mockito.`when`(mockFile.originalFilename).thenReturn(null)

        assertFalse(parser.supports(mockFile))
    }

    @Test
    fun parseValidCSV() {
        val csvContent = Fixture.fromFile("users-01.csv")
        val file = MockMultipartFile("file", "users-01.csv", "text/csv", csvContent.toByteArray())

        val actualResult = parser.parse(file)

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
    fun parseThrowsExceptionForEmptyFile() {
        val file = MockMultipartFile("file", "empty.csv", "text/csv", byteArrayOf())

        val actualException = assertThrows(CsvFileCannotBeReadException::class.java) {
            parser.parse(file)
        }
        val expectedMessage = "File is empty"
        assertEquals(expectedMessage, actualException.message)
    }

    @Test
    fun parseThrowsExceptionForIOException() {
        val mockFile = Mockito.mock(MultipartFile::class.java)
        Mockito.`when`(mockFile.isEmpty).thenReturn(false)
        Mockito.`when`(mockFile.inputStream).thenThrow(IOException("IO error"))

        val actualException = assertThrows(CsvFileCannotBeReadException::class.java) {
            parser.parse(mockFile)
        }
        val expectedMessage = "File cannot be read"
        assertEquals(expectedMessage, actualException.message)
    }
}