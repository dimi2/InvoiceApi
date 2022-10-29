# InvoiceApi

InvoiceApi - Playing with SpringBoot and REST.

## How to build the application

Pre-conditions:
1. Installed [JDK 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html).
2. Installed [Gradle 7+](https://gradle.org/install/).

Build steps:
1. Download the source code or clone this repository on your machine.
2. Go to the directory where the project is extracted/cloned.
3. Open terminal (console) at that project root directory.
4. Execute command `./gradlew build` (for Linux) `gradlew build` (for Windows).

## How to execute the application tests

1. Open terminal (console) at the project directory.
2. Execute command `./gradlew test` (for Linux) `gradlew test` (for Windows).

During tests execution you may notice some exceptions - these are expected (result of negative tests).

## How to run the application

1. Open terminal (console) at that directory.
2. Execute `./gradlew bootRun` (for Linux) `gradlew bootRun` (for Windows).
3. The application REST endpoint will be available at: http://localhost:8080/api/v1/sumInvoices .
