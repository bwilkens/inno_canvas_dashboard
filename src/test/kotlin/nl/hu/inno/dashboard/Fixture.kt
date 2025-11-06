package nl.hu.inno.dashboard

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import java.io.IOException
import java.nio.file.Files

class Fixture {
    companion object {
        fun fromFile(name: String): Resource {
            try {
                return ClassPathResource("fixtures/$name")
            } catch (exception: IOException) {
                throw RuntimeException("Could not read fixture '$name'")
            }
        }
    }
}