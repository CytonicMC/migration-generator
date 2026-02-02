package net.cytonic.migrationGenerator

import io.ebean.annotation.DbName
import io.ebean.annotation.Platform
import io.github.classgraph.ClassGraph

object MigrationGeneratorMain {
    @JvmStatic
    fun main(args: Array<String>) {
        require(args.size >= 6) { "Expected 6 arguments" }

        val database = args[0]
        val pluginId = args[1]
        val platform = Platform.valueOf(args[2])
        val basePath = args[3]
        val resourcesPath = args[4]
        val packages = args[5].split(",")

        println("Generating migration for $database database...")

        val filteredClasses = findEntitiesForDatabase(database, packages)

        if (filteredClasses.isEmpty()) {
            println("No entities found for $database database - skipping migration generation")
            return
        }

        println("Found ${filteredClasses.size} entities for $database: ${filteredClasses.map { it.simpleName }}")

        MigrationGenerator.run(database, pluginId, platform, basePath, resourcesPath, filteredClasses)

        println("Migration generated for $database database")
    }

    private fun findEntitiesForDatabase(targetDatabase: String, packages: List<String>): Set<Class<*>> {
        val entities = mutableSetOf<Class<*>>()
        val classLoader = Thread.currentThread().contextClassLoader

        packages.forEach { packageName ->
            val scanResult = ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(packageName)
                .overrideClassLoaders(classLoader)
                .scan()

            scanResult.use { result ->
                val entityClasses = result.getClassesWithAnnotation("jakarta.persistence.Entity")

                for (classInfo in entityClasses) {
                    val clazz = classInfo.loadClass()
                    val dbNameAnnotation = clazz.getAnnotation(DbName::class.java)

                    val entityDatabase = dbNameAnnotation?.value ?: "environment"

                    if (entityDatabase == targetDatabase) {
                        entities.add(clazz)
                    }
                }
            }
        }

        return entities
    }
}