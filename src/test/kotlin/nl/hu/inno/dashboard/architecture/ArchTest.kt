package nl.hu.inno.dashboard.architecture

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.Test

class ArchTest {
    @Test
    fun intendedArchitecture_isRespected() {
        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages("nl.hu.inno.dashboard")

        layeredArchitecture()
            .consideringAllDependencies()
            .layer("Controller").definedBy("..presentation..")
            .layer("Service").definedBy("..application..")
            .layer("Domain").definedBy("..domain..")
            .layer("Persistence").definedBy("..data..")

            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Controller").mayOnlyAccessLayers("Service")

            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service")
            .whereLayer("Service").mayOnlyAccessLayers("Service", "Domain", "Persistence")

            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Service", "Persistence")

            .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service")
            .whereLayer("Persistence").mayOnlyAccessLayers("Domain")

            .ignoreDependency(
                JavaClass.Predicates.resideInAnyPackage("nl.hu.inno.dashboard.."),
                JavaClass.Predicates.resideInAnyPackage(
                    "java..",
                    "jakarta..",
                    "org.springframework..",
                    "org.apache.commons..",
                    "kotlin..",
                    "org.jetbrains..",
                    "nl.hu.inno.dashboard.exception.."
                )
            )

            .check(importedClasses)
    }
}