package net.cytonic.migrationGenerator

import io.ebean.annotation.Platform
import io.ebean.config.DatabaseConfig
import io.ebean.dbmigration.DbMigration
import java.io.IOException
import kotlin.system.exitProcess

/**
 * Main class for generating Ebean migrations. Called by the Gradle plugin tasks.
 */
object MigrationGenerator {
    @JvmStatic
    fun run(
        database: String,
        pluginId: String,
        platform: Platform,
        basePath: String,
        resourcesPath: String,
        entityClasses: List<Class<*>>
    ) {
        val migration = DbMigration.create()
        val config = DatabaseConfig()
        config.name = database

        entityClasses.forEach { config.addClass(it) }

        migration.setServerConfig(config)
        migration.setPlatform(platform)
        migration.setPathToResources(resourcesPath)
        migration.setMigrationPath("$basePath/$pluginId/$database")

        try {
            migration.generateMigration()
        } catch (e: IOException) {
            System.err.println("✗ Failed to generate migration: " + e.message)
            e.printStackTrace()
            exitProcess(1)
        }
    }
}