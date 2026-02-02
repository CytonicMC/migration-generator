package net.cytonic.migrationGenerator

import io.ebean.DatabaseFactory
import io.ebean.annotation.Platform
import io.ebean.config.DatabaseConfig
import io.ebean.datasource.DataSourceConfig
import io.ebean.dbmigration.DbMigration

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
        entityClasses: Set<Class<*>>
    ) {
        System.setProperty("ebean.ignoreExtraDdl", "false")
        System.setProperty("datasource.default", "h2")

        val config = DatabaseConfig()
        config.name = "migration_$database"
        config.isDefaultServer = false
        config.isRegister = true
        config.packages = emptyList()
        config.setLoadModuleInfo(false)
        config.disableClasspathSearch(true)
        config.databasePlatformName = platform.name.lowercase()

        val dsConfig = DataSourceConfig()
        dsConfig.driver = "org.h2.Driver"
        dsConfig.url = "jdbc:h2:mem:test"
        dsConfig.username = "sa"
        dsConfig.password = ""
        config.setDataSourceConfig(dsConfig)

        entityClasses.forEach { config.addClass(it) }

        val ebeanServer = DatabaseFactory.create(config)

        try {
            val migration = DbMigration.create()
            migration.setServer(ebeanServer)
            migration.setPlatform(platform)
            migration.setPathToResources(resourcesPath)
            migration.setMigrationPath("$basePath/$pluginId/$database")
            migration.setStrictMode(true)

            migration.generateMigration()
        } finally {
            ebeanServer.shutdown()
        }
    }
}