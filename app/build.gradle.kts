plugins {
    application
    kotlin("jvm") version "2.1.21"
    java
}

group = "org.db_poultry"
version = "4.0"
val appMainClass = "org.db_poultry.AppKt"

application {
    mainClass.set(appMainClass)
}

// JavaFX setup
val javafxVersion = "23.0.1"
val platform = when (System.getProperty("os.name").lowercase()) {
    "mac os x", "macos" -> "mac"
    "linux" -> "linux"
    else -> "win"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    val javafxModules = listOf("base", "graphics", "controls", "fxml")
    javafxModules.forEach {
        implementation("org.openjfx:javafx-$it:$javafxVersion:$platform")
    }

    implementation("org.kordamp.ikonli:ikonli-core:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-javafx:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-fontawesome6-pack:12.4.0")
    implementation("org.controlsfx:controlsfx:11.2.2")

    implementation("org.postgresql:postgresql:42.7.3")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation(kotlin("test"))
}

sourceSets {
    main {
        java.srcDirs("src/main/java")
        kotlin.srcDirs("src/main/kotlin")
    }
    test {
        java.srcDirs("src/test/java")
        kotlin.srcDirs("src/test/kotlin")
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
}

// Native (jpackage) packaging
tasks.register<Exec>("jpackage") {
    dependsOn("build")

    val jarName = "db-poultry-${project.version}.jar"
    val imageName = "DB-Poultry"
    val installerName = "DBP-Installer"

    commandLine(
        "jpackage",
        "--input", "${layout.buildDirectory.asFile.get()}/libs",
        "--name", imageName,
        "--main-jar", jarName,
        "--main-class", appMainClass,
        "--type", "exe",
        "--app-version", project.version.toString(),
        "--dest", "${layout.buildDirectory.asFile.get()}/installer"
    )
}
