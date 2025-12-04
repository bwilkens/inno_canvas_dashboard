package nl.hu.inno.dashboard.fileparser.application

import nl.hu.inno.dashboard.Fixture
import nl.hu.inno.dashboard.fileparser.domain.CsvFileParser
import nl.hu.inno.dashboard.exception.exceptions.FileTypeNotSupportedException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FileParserServiceImplTest {

    private lateinit var csvParser: CsvFileParser
    private lateinit var service: FileParserService

    @BeforeEach
    fun setUp() {
        csvParser = CsvFileParser()
        service = FileParserServiceImpl(listOf(csvParser))
    }

    @Test
    fun parseFile_returnsParsedRecords_forValidCsvResource() {
        val csvResource = Fixture.fromFile("users-01.csv")

        val actualResult = service.parseFile(csvResource)

        val expectedResultSize = 6
        val expectedFirstRow = listOf("50304","Innovation Semester - September 2025","TICT-V3SE6-25_SEP25","2025-09-01 00:00:00+02:00","2026-01-30 23:59:59+01:00","John Doe","john.doe@student.hu.nl","STUDENT")
        val firstResultRow = 0
        val expectedSecondRow = listOf("50304","Innovation Semester - September 2025","TICT-V3SE6-25_SEP25","2025-09-01 00:00:00+02:00","2026-01-30 23:59:59+01:00","John Doe","john.doe@student.hu.nl","STUDENT")
        val secondResultRow = 1
        val expectedThirdRow = listOf("9999","Test cursus - September 2010","TEST-9999_SEP25","2010-09-01 00:00:00+02:00","2011-01-30 23:59:59+01:00","John Doe","john.doe@student.hu.nl","STUDENT")
        val thirdResultRow = 2
        val expectedFourthRow = listOf("50304","Innovation Semester - September 2025","TICT-V3SE6-25_SEP25","2025-09-01 00:00:00+02:00","2026-01-30 23:59:59+01:00","Jane Doe","jane.doe@hu.nl","TEACHER")
        val fourthResultRow = 3
        val expectedFifthRow = listOf("50304","Innovation Semester - September 2025","TICT-V3SE6-25_SEP25","2025-09-01 00:00:00+02:00","2026-01-30 23:59:59+01:00","User Null","null","STUDENT")
        val fifthResultRow = 4
        val expectedSixthRow = listOf("9999","Test cursus - September 2010","TEST-9999_SEP25","2010-09-01 00:00:00+02:00","2011-01-30 23:59:59+01:00","Test User","test.user@student.hu.nl","STUDENT")
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
    fun parseFile_throwsException_forUnsupportedFileType() {
        val txtResource = Fixture.fromFile("users-01.txt")

        val actualException = assertThrows(FileTypeNotSupportedException::class.java) {
            service.parseFile(txtResource)
        }
        val expectedMessage = "No available parser found for file: users-01.txt"
        assertEquals(expectedMessage, actualException.message)
    }
}