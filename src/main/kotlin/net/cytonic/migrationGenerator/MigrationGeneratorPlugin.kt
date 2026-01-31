package net.cytonic.migrationGenerator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import java.util.Locale.getDefault

class MigrationGeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension: EbeanMigrationExtension =
            project.extensions.create("migration", EbeanMigrationExtension::class.java)

        extension.id.convention(project.name)
        extension.databases.convention(listOf("environment"))
        extension.migrationBasePath.convention("dbmigration")
        extension.resourcesPath.convention("src/main/resources")
        extension.entityPackages.convention(listOf("net.cytonic"))
        project.tasks.register("generateMigrations") {
            group = "migrations"
            description = "Generate migrations for all configured databases"

            dependsOn(extension.databases.get().map { "generateMigration${it.capitalize()}" })
        }

        project.afterEvaluate {
            extension.databases.get().forEach { createMigrationTask(project, extension, it) }
        }
    }

    private fun createMigrationTask(project: Project, extension: EbeanMigrationExtension, database: String) {
        project.tasks.register("generateMigration${database.capitalize()}", JavaExec::class.java) {
            group = "migrations"
            description = "Generate migrations for $database database"

            dependsOn("classes")

            val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
            val mainSourceSet = sourceSets.getByName("main")

            classpath = project.buildscript.configurations.getByName("classpath") + mainSourceSet.runtimeClasspath

            mainClass.set("net.cytonic.migrationGenerator.MigrationGeneratorMain")

            args(
                database,
                extension.id.get(),
                extension.platform.get().name,
                extension.migrationBasePath.get(),
                extension.resourcesPath.get(),
                extension.entityPackages.get().joinToString(",")
            )
        }
    }

    fun String.capitalize(): String {
        return replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString()
        }
    }
}
