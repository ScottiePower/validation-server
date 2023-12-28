import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.text.SimpleDateFormat
import java.util.*

plugins {
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.21"
	kotlin("plugin.spring") version "1.9.21"
}

group = "com.example"
version = SimpleDateFormat("yyyyddMM.hhmmss").format(Date())

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

val builtImageNameOutput = File(buildDir, "applicationImageName")

tasks {

	withType<Test> { useJUnitPlatform() }

	register("getImageName") { doLast { println(builtImageNameOutput.readText()) } }

	bootBuildImage {
		val registryUrl = "docker.io"
		val registryUserName = "zimmy71"
		val repositoryName = "zimmy71/validation-server"
		imageName.set("$registryUrl/$repositoryName:${project.version}")
		val registryPassword = getRequiredProperty("registryPassword")
		docker {
			publishRegistry {
				url.set(registryUrl)
				username.set(registryUserName)
				password.set(registryPassword)
			}
		}
		doLast { builtImageNameOutput.writeText(imageName.get()) }
	}

}

fun Project.getRequiredProperty(property: String): String {
	return this.findProperty(property) as String?
		?: throw GradleException("'$property' must be defined")
}