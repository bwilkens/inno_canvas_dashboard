package nl.hu.inno.dashboard.dashboard.application

import nl.hu.inno.dashboard.dashboard.data.CourseRepository
import nl.hu.inno.dashboard.dashboard.data.UsersRepository
import nl.hu.inno.dashboard.dashboard.domain.Course
import nl.hu.inno.dashboard.dashboard.domain.Role
import nl.hu.inno.dashboard.fileparser.application.FileParserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import nl.hu.inno.dashboard.dashboard.domain.Users
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.argThat
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.mock.web.MockMultipartFile
import java.time.LocalDate
import java.util.Optional

class DashboardServiceImplTest {
    private lateinit var courseDB: CourseRepository
    private lateinit var usersDB: UsersRepository
    private lateinit var fileParserService: FileParserService
    private lateinit var service: DashboardServiceImpl

    @BeforeEach
    fun setUp() {
        courseDB = mock()
        usersDB = mock()
        fileParserService = mock()
        service = DashboardServiceImpl(courseDB, usersDB, fileParserService)
    }

    @Test
    fun updateUsersInCourse() {
        val mockFile = MockMultipartFile(
            "file",
            "test.csv",
            "text/csv",
            ByteArray(0)
        )

        val course = Course(
            canvasId = 50304,
            title = "Innovation Semester - September 2025",
            startDate = LocalDate.parse("2025-09-01"),
            endDate = LocalDate.parse("2026-01-30")
        )

        val parsedRecords = listOf(
            listOf(
                "50304", "Innovation Semester - September 2025",
                "2025-09-01 00:00:00+02:00", "2026-01-30 23:59:59+01:00",
                "John Doe", "john.doe@student.hu.nl", "STUDENT"
            ),
            listOf(
                "50304", "Innovation Semester - September 2025",
                "2025-09-01 00:00:00+02:00", "2026-01-30 23:59:59+01:00",
                "Jane Doe", "jane.doe@hu.nl", "TEACHER"
            )
        )

        `when`(fileParserService.parseFile(mockFile)).thenReturn(parsedRecords)
        `when`(courseDB.findById(50304)).thenReturn(Optional.empty())
        `when`(courseDB.save(any(Course::class.java))).thenReturn(course)

        Mockito.doAnswer { invocation ->
            val users = invocation.getArgument<Iterable<Users>>(0)
            users
        }.`when`(usersDB).saveAll(anyList())

        service.updateUsersInCourse(mockFile)

        verify(fileParserService, times(1)).parseFile(mockFile)
        verify(courseDB, times(1)).save(any(Course::class.java))
        verify(usersDB, times(1)).saveAll(
            argThat { users: Iterable<Users> ->
                users.any { it.emailAddress == "john.doe@student.hu.nl" && it.role == Role.STUDENT } &&
                        users.any { it.emailAddress == "jane.doe@hu.nl" && it.role == Role.TEACHER }
            }
        )
    }

    @Test
    fun addUsersToCourseTest() {
        val mockFile = MockMultipartFile(
            "file",
            "test.csv",
            "text/csv",
            ByteArray(0)
        )

        val course = Course(
            canvasId = 50304,
            title = "Innovation Semester - September 2025",
            startDate = LocalDate.parse("2025-09-01"),
            endDate = LocalDate.parse("2026-01-30"),
            users = emptySet()
        )

        val parsedRecords = listOf(
            listOf(
                "50304", "Innovation Semester - September 2025",
                "2025-09-01 00:00:00+02:00", "2026-01-30 23:59:59+01:00",
                "John Doe", "john.doe@student.hu.nl", "STUDENT"
            ),
            listOf(
                "50304", "Innovation Semester - September 2025",
                "2025-09-01 00:00:00+02:00", "2026-01-30 23:59:59+01:00",
                "Jane Doe", "jane.doe@hu.nl", "TEACHER"
            )
        )

        `when`(fileParserService.parseFile(mockFile)).thenReturn(parsedRecords)
        `when`(courseDB.findById(50304)).thenReturn(Optional.of(course))
        `when`(courseDB.save(any(Course::class.java))).thenAnswer { it.getArgument(0) }

        Mockito.doAnswer { invocation ->
            val users = invocation.getArgument<Iterable<Users>>(0)
            users
        }.`when`(usersDB).saveAll(anyList())

        service.addUsersToCourse(mockFile)

        verify(fileParserService, times(1)).parseFile(mockFile)
        verify(courseDB, times(1)).save(argThat { it.users.size == 2 })
        verify(usersDB, times(1)).saveAll(
            argThat { users: Iterable<Users> ->
                users.any { it.emailAddress == "john.doe@student.hu.nl" && it.role == Role.STUDENT } &&
                        users.any { it.emailAddress == "jane.doe@hu.nl" && it.role == Role.TEACHER }
            }
        )
    }

}