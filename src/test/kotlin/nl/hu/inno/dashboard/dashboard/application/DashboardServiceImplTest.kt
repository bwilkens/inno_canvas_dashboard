package nl.hu.inno.dashboard.dashboard.application

import nl.hu.inno.dashboard.dashboard.data.CourseRepository
import nl.hu.inno.dashboard.dashboard.data.UsersRepository
import nl.hu.inno.dashboard.fileparser.application.FileParserService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class DashboardServiceImplTest {
    private val courseRepository: CourseRepository = mock()
    private val usersRepository: UsersRepository = mock()
    private val fileParserService: FileParserService = mock()

    @Test
    fun updateExistingCourseData() {

    }

//TODO    Waiting for completion
//    @Test
//    fun replaceCourseData() {
//    }

}