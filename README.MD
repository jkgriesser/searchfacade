# Search Facade - Language Edition
This is a Spring Boot REST application running on port 6868, connecting to a dockerised MongoDB database listening on port 27777.

## REST Endpoints
The following services (all **GET**) are provided:

* _/_ &rarr; Simple greeting
* _/languages/{id}_ &rarr; Find single language by ID
* _/languages/search_ &rarr; Search language(s) by filter parameter(s)

## Start
Note: These instructions presume you already have **JDK 8** and **Docker** installed.

To start the application, first run **./mongodocker.sh**, which will launch MongoDB inside a Docker container.
Then run **./gradlew bootRun** to launch the web application.

## Stop
To stop the application, simply terminate the web server and press a key in the terminal where the docker script is running.

## Test
To test the application, run **./gradlew check**.

## Default Data
The **mongodocker.sh** script sets up a new database named **search_facade** and creates a collection named **language**.
This collection is then populated with sample data taken from [sampledata/language.json](sampledata/language.json).

## Find by ID
To find by ID, issue a **GET** request as follows:

>_GET /languages/{id}_

**{id}** takes the form of the MongoDB **_id** attribute of the relevant language.

## Search by Filter
To search by filter, issue a **GET** request as follows:

>_GET /languages/search?filter=filter1&filter=filter2&filter=filter3..._

Filters themselves are JSON objects. The following format is observed:
```json
{ "attribute": "value1",
  "operator": "value2",
  "value": "value3",
  "range": {
    "from": "value4",
    "to": "value5"
  }
}
```
**attribute** is the name of a MongoDB collection field, and is also one of two mandatory fields, alongside **operator**. Either a **value** or a **range** must be provided but never both at once.

Searches are conjunctive, i.e. the filters are combined via a logical AND operation.

### Operators
One of the following operators must be used:

* **eq**: equals
* **gte**: greater than or equals
* **lte**: less than or equals

When searching by range, only **eq** is a valid operator.

## Todo
* Security (e.g. MongoDB credentials, API keys, etc.)
* MongoDB: Schema, indices, authentication, etc.
* Rate limiting and limiting query results (i.e. pagination)
* Results sorting
* A custom error page for unknown REST paths (HTTP 404)
* Logging
* JUnit 5 integration (e.g. @DisplayName annotations for easy-to-read test descriptions)
* Refactor / clean-up of Test classes

etc.

## Known issues
The following version of Java appears to be incompatible with the [LanguageControllerTests](src/test/java/io/tradeledger/searchfacade/LanguageControllerTests.java) mock setup: Java(TM) SE Runtime Environment (build 1.8.0-b132).

<!-- language: none -->
    org.mockito.exceptions.base.MockitoException: 
    Mockito cannot mock this class: interface io.tradeledger.searchfacade.repository.LanguageRepository.
 
 Later versions work as expected.
 