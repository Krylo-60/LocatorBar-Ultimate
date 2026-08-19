plugins {
    `java`
}

group = "pl.fuzjajadrowa.locatorbar"
version = "1.2.3"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.withType<Jar> {
    archiveFileName.set("LocatorBar-Paper-${project.version}.jar")
}
