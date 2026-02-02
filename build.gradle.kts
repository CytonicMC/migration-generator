plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "net.cytonic"
version = "1.0-SNAPSHOT"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("io.ebean:ebean:17.2.0")
    implementation("io.ebean:ebean-ddl-generator:17.2.1")
    implementation("io.ebean:ebean-migration:14.3.0")
    implementation("io.github.classgraph:classgraph:4.8.184")
    implementation("com.h2database:h2:2.4.240")
}

gradlePlugin {
    plugins {
        register("migration-generator") {
            id = "net.cytonic.migration-generator"
            implementationClass = "net.cytonic.migrationGenerator.MigrationGeneratorPlugin"
        }
    }
}

publishing {
    repositories {
        maven {
            name = "FoxikleCytonicRepository"
            url = uri("https://repo.foxikle.dev/cytonic")
            var u = System.getenv("REPO_USERNAME")
            var p = System.getenv("REPO_PASSWORD")

            if (u == null || u.isEmpty()) {
                u = "no-value-provided"
            }
            if (p == null || p.isEmpty()) {
                p = "no-value-provided"
            }

            val user = providers.gradleProperty("FoxikleCytonicRepositoryUsername").orElse(u).get()
            val pass = providers.gradleProperty("FoxikleCytonicRepositoryPassword").orElse(p).get()
            credentials {
                username = user
                password = pass
            }
            authentication {
                create<BasicAuthentication>("basic") {

                }
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xsuppress-version-warnings")
    }
}