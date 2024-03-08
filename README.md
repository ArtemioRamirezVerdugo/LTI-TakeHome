### Swagger

http://localhost:8081/swagger-ui/index.html

### Relevant Gradle Tasks

    * build/build - Builds the project
    * other/checkstyleMain - Runs code style checks
    * verification/jacocoTestReport - Generates code coverage report
    * verification/jacodoTestCoverageVerification - Enforces code coverage of 85%
    * spotbugsMain - Runs static analysis checks for anti-patterns and code smells

### Notes

    * Using SQLite so that there is no need for a db server
    * DB is located at resources/movies.db has a few records for testing purposes
    * resources/demoSetup.sql can be used to setup a new db from scratch