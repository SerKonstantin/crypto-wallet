import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	application
	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.5"
	id("checkstyle")
	id("jacoco")
	id("io.freefair.lombok") version "8.6"
	id("com.github.johnrengelman.shadow") version "8.1.1" // TODO configure fat jar generation for deploy
	id("com.github.ben-manes.versions") version "0.50.0"
}

group = "com.konstantin"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.web3j:core:5.0.0")
	implementation("org.web3j:crypto:5.0.0")

	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

	runtimeOnly("org.postgresql:postgresql:42.7.3")
	runtimeOnly("com.h2database:h2:2.2.224")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.instancio:instancio-junit:4.5.0")
	implementation("net.datafaker:datafaker:2.1.0")

	implementation("org.springframework.boot:spring-boot-starter-logging")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	implementation ("io.github.cdimascio:dotenv-java:3.0.0") // Read local api key from .env for tests
	implementation("net.gcardone.junidecode:junidecode:0.5.1") // For wallet slugs
}

application {
	mainClass.set("com.konstantin.crypto_wallet.CryptoWalletApplication")
}

tasks.withType<Test> {
	useJUnitPlatform()
	testLogging {
		exceptionFormat = TestExceptionFormat.FULL
		events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.STANDARD_OUT)
		showStandardStreams = true
	}
}

checkstyle {
	toolVersion = "10.17.0"
	configFile = file("config/checkstyle/checkstyle.xml")
}

jacoco {
	toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.outputLocation.set(file("build/reports/jacoco"))
	}
}
