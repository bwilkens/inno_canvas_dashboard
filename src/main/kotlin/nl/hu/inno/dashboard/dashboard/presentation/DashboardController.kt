package nl.hu.inno.dashboard.dashboard.presentation

import nl.hu.inno.dashboard.dashboard.application.DashboardServiceImpl
import nl.hu.inno.dashboard.dashboard.domain.Course
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dashboard/")
class DashboardController(private val service: DashboardServiceImpl) {
    @GetMapping("/{id}")
    fun getMessage(@PathVariable id: Int): ResponseEntity<Course> {
        val course = service.findCourseById(id)
        return if (course != null) {
            ResponseEntity.ok(course)
        } else
            ResponseEntity.notFound().build()
    }
}