package nl.hu.inno.dashboard.dashboard.presentation

import nl.hu.inno.dashboard.dashboard.application.DashboardServiceImpl
import nl.hu.inno.dashboard.dashboard.domain.Course
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/dashboard/")
class DashboardController(
    private val service: DashboardServiceImpl
    ) {

    @GetMapping("/{id}")
    fun getMessage(@PathVariable id: Int): ResponseEntity<Course> {
        val course = service.findCourseById(id)
        return if (course != null) {
            ResponseEntity.ok(course)
        } else
            ResponseEntity.notFound().build()
    }

    @PostMapping("/add")
    fun addCourse(): ResponseEntity<Void> {
        return try {
            service.addUsersToCourse()
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @PostMapping("/update")
    fun updateCourse(): ResponseEntity<Void> {
        return try {
            service.updateUsersInCourse()
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }
}