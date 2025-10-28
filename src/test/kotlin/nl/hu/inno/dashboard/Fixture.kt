package nl.hu.inno.dashboard

import org.springframework.core.io.ClassPathResource
import java.io.IOException
import java.nio.file.Files

class Fixture {
    companion object {
        fun fromFile(name: String): String {
            try {
                val file = ClassPathResource("fixtures/$name").file
                return Files.readString(file.toPath())
            } catch (exception: IOException) {
                throw RuntimeException("Could not read fixture '$name'")
            }
        }
    }
}