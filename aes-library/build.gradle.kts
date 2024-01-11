import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
    id("com.google.devtools.ksp").version("1.9.21-1.0.16")
    id("maven-publish")
}

val artifactVersion = "0.0.5"
val groupName = "com.github.asadullahilyas"

group = groupName
version = artifactVersion

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = groupName
                artifactId = "KotlinSymmetricEncryption"
                version = artifactVersion

                from(components["java"])
            }
        }
        repositories {
            mavenLocal()
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}