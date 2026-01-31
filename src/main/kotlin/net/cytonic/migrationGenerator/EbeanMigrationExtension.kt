package net.cytonic.migrationGenerator

import io.ebean.annotation.Platform
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class EbeanMigrationExtension {

    /**
     * The plugin/project ID (e.g., 'cytosis' or 'my-awesome-plugin'). Used in the migration path:
     * dbmigration/{pluginId}/{database}
     */
    abstract val id: Property<String>
    abstract val platform: Property<Platform>
    abstract val databases: ListProperty<String>

    /**
     * Base path for migrations (relative to resources path). Default: 'dbmigration'
     */
    abstract val migrationBasePath: Property<String>

    /**
     * Path to resources directory. Default: 'src/main/resources'
     */
    abstract val resourcesPath: Property<String>
    abstract val entityPackages: ListProperty<String>
}
