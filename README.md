# Technical Test using Spring

### Steps to run:
* use command 
> gradlew bootRun
* Or, Import as Gradle project in IDE, & run Application.java

---
Running the application achieves two things
  1. starts spring boot application, and
  2. executes client code to test the REST endpoints for getting, creating, updating data

---
#### Details
* Application is Built upon Spring Boot 2
* the dependencies or the build.gradle have not been modified
* Input Validations are put in place with a central Error handling. Error Handling and the ErrorReponse can be expanded upon as the Application grows
* The External Rest Call/s (Call to Hadoop Data Lake) are made Asynchronously
* Application is thoroughly Unit tested (using JUnit 5/Jupiter, Mockito, AspectJ, spring-boot-test)
* Application is integration tested for the Controller (using Spring MockMvc)
---

#### Ideas to enhance upon
Since the task prohibited adding any new build dependencies, following ideas could not be implemented
* Service Registration and Discovery using Eureka -
Requires dependencies:
  * spring-cloud-starter-netflix-eureka-server &
  * spring-cloud-starter-netflix-eureka-client

* Spring Security using Basic Security / OAuth2 - requires dependencies
  * spring-boot-starter-security\
  OR
  * spring-boot-starter-oauth2-server
  * spring-boot-starter-oauth2-client
