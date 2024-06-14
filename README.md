# maSéance - Screening Service

## Description

A Java project using Spring Boot and Gradle.
The project is a RESTful API that can be connected to maSéance front-end project. 

It allows to create, read, update, and delete screenings.
Movies informations can be retrieved from the [The Movie Database API](https://www.themoviedb.org/documentation/api).

## Installation

### Prerequisites

- Java 8 or higher
- Gradle
- Docker

### Steps

1. Clone the repository
2. Navigate to the project directory
3. Run `gradle build`

## Usage

Use docker compose file to get PostgreSQL running.
To run the application, use the command `gradle bootRun`.
