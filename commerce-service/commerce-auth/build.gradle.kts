plugins {
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("com.palantir.docker") version "0.35.0"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    compileOnly("org.springframework:spring-tx")

    implementation("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

    implementation(project(":commons:common-util"))
    implementation(project(":commons:model"))
    testImplementation(project(":commons:test-helper"))
    implementation(project(":commons:persistence-database"))
    implementation(project(":commons:common-web"))
    implementation(project(":commons:common-jwt"))
}

val asciidoctorExt: Configuration by configurations.creating
dependencies {
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
}

val snippetsDir by extra { file("build/generated-snippets") }

tasks {
    test {
        outputs.dir(snippetsDir)
    }

    asciidoctor {
        inputs.dir(snippetsDir)
        configurations(asciidoctorExt.name)
        baseDirFollowsSourceFile()
        dependsOn(test)
        doLast {
            copy {
                from("build/docs/asciidoc")
                into("build/resources/main/static/docs")
            }
        }
    }

    bootJar {
        dependsOn(asciidoctor)
    }
}

docker {
    val dockerImageName = project.findProperty("dockerImageName") as String?

    name = dockerImageName ?: "${project.name}:${version}"
    setDockerfile(file("../../Dockerfile"))
    files(tasks.bootJar.get().outputs.files)
    buildArgs(mapOf(
        "JAR_FILE" to tasks.bootJar.get().outputs.files.singleFile.name
    ))
}