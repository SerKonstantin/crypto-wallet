.PHONY: build test lint report run

run:
	cd backend && ./gradlew bootRun

build:
	cd backend && ./gradlew build

lint:
	cd backend && ./gradlew checkstyleMain checkstyleTest

test:
	cd backend && ./gradlew test

report:
	cd backend && ./gradlew jacocoTestReport

on-push:
	cd backend && ./gradlew build && ./gradlew checkstyleMain checkstyleTest && ./gradlew test

