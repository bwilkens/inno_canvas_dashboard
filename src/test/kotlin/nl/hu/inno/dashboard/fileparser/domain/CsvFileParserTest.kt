package nl.hu.inno.dashboard.fileparser.domain

import nl.hu.inno.dashboard.Fixture
import nl.hu.inno.dashboard.fileparser.domain.exception.EmptyFileException
import nl.hu.inno.dashboard.fileparser.domain.exception.FileCannotBeReadException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.core.io.Resource
import java.io.IOException

class CsvFileParserTest {

    private lateinit var parser: CsvFileParser

    @BeforeEach
    fun setup() {
        parser = CsvFileParser()
    }

    @Test
    fun supports_returnsTrue_forCsvResource() {
        val resource = Fixture.fromFile("users-01.csv")

        assertTrue(parser.supports(resource))
    }

    @Test
    fun supports_returnsFalse_forNonCsvResource() {
        val resource = Fixture.fromFile("users-01.txt")

        assertFalse(parser.supports(resource))
    }

    @Test
    fun supports_returnsFalse_forNullFilename() {
        val mockResource = Mockito.mock(Resource::class.java)

        Mockito.`when`(mockResource.filename).thenReturn(null)

        assertFalse(parser.supports(mockResource))
    }

    @Test
    fun parse_returnsParsedRecords_forValidCsvResource() {
        val resource = Fixture.fromFile("users-01.csv")

        val actualResult = parser.parse(resource)

        val expectedResultSize = 6
        val expectedFirstRow = listOf("50304","Innovation Semester - September 2025","2025-09-01 00:00:00+02:00","2026-01-30 23:59:59+01:00","John Doe","john.doe@student.hu.nl","STUDENT")
        val firstResultRow = 0
        val expectedSecondRow = listOf("50304","Innovation Semester - September 2025","2025-09-01 00:00:00+02:00","2026-01-30 23:59:59+01:00","John Doe","john.doe@student.hu.nl","STUDENT")
        val secondResultRow = 1
        val expectedThirdRow = listOf("9999","Test cursus - September 2010","2010-09-01 00:00:00+02:00","2011-01-30 23:59:59+01:00","John Doe","john.doe@student.hu.nl","STUDENT")
        val thirdResultRow = 2
        val expectedFourthRow = listOf("50304","Innovation Semester - September 2025","2025-09-01 00:00:00+02:00","2026-01-30 23:59:59+01:00","Jane Doe","jane.doe@hu.nl","TEACHER")
        val fourthResultRow = 3
        val expectedFifthRow = listOf("50304","Innovation Semester - September 2025","2025-09-01 00:00:00+02:00","2026-01-30 23:59:59+01:00","User Null","null","STUDENT")
        val fifthResultRow = 4
        val expectedSixthRow = listOf("9999","Test cursus - September 2010","2010-09-01 00:00:00+02:00","2011-01-30 23:59:59+01:00","Test User","test.user@student.hu.nl","STUDENT")
        val sixthResultRow = 5
        assertEquals(expectedResultSize, actualResult.size)
        assertEquals(expectedFirstRow, actualResult[firstResultRow])
        assertEquals(expectedSecondRow, actualResult[secondResultRow])
        assertEquals(expectedThirdRow, actualResult[thirdResultRow])
        assertEquals(expectedFourthRow, actualResult[fourthResultRow])
        assertEquals(expectedFifthRow, actualResult[fifthResultRow])
        assertEquals(expectedSixthRow, actualResult[sixthResultRow])
    }

    @Test
    fun parse_throwsException_forEmptyResource() {
        val resource = Fixture.fromFile("empty.csv")

        val actualException = assertThrows(EmptyFileException::class.java) {
            parser.parse(resource)
        }
        val expectedMessage = "File is empty or does not exist"
        assertEquals(expectedMessage, actualException.message)
    }

    @Test
    fun parse_throwsException_forIoException() {
        val mockResource = Mockito.mock(Resource::class.java)

        Mockito.`when`(mockResource.exists()).thenReturn(true)
        Mockito.`when`(mockResource.contentLength()).thenReturn(1L)
        Mockito.`when`(mockResource.inputStream).thenThrow(IOException("IO error"))
        val actualException = assertThrows(FileCannotBeReadException::class.java) {
            parser.parse(mockResource)
        }
        val expectedMessage = "File cannot be read"
        assertEquals(expectedMessage, actualException.message)
    }
}