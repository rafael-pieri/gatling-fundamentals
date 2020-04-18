package simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class AuthorFullTest extends Simulation {

  private val httpConf = http
    .baseUrl("http://localhost:8081")
    .header("Accept", "application/json")

  /*** Variables ***/
  // runtime variables
  def userCount: Int = getProperty("USERS", "3").toInt
  def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt
  def testDuration: Int = getProperty("DURATION", "60").toInt

  /*** Helper Methods ***/
  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  /*** Custom Feeder ***/
  private val customFeeder = Iterator.continually(Map(
    "name" -> "Clarice Lispector",
    "nationality" -> "Ucraniana"
  ))

  /*** Before ***/
  before {
    println(s"Running test with ${userCount} users")
    println(s"Ramping users over ${rampDuration} seconds")
    println(s"Total Test duration: ${testDuration} seconds")
  }

  /*** HTTP Calls ***/
  private def getAllAuthors = {
    exec(
      http("Get all authors")
        .get("/authors")
        .check(status.is(200)))
  }

  private def postNewAuthorAndThenDelete() = {
    feed(customFeeder)
      .exec(http("Post Author")
        .post("/authors")
        .body(ElFileBody("request-bodies/NewAuthorTemplate.json")).asJson
        .check(status.is(201))
        .check(bodyString.saveAs("responseBody"))
        .check(jsonPath("$.id").saveAs("authorId")))
      .exec { session => println(session("responseBody").as[String]); session }

      .exec(http("Get last posted author")
        .get("/authors/${authorId}")
        .check(jsonPath("$.name").is("${name}"))
        .check(status.is(200)))

      .exec(http("Delete last posted author")
          .delete("/authors/${authorId}")
          .check(status.is(204)))
  }

  /*** Scenario Design ***/
  private val scenarioBuilder = scenario("Author DB")
    .forever() {
      exec(getAllAuthors)
        .pause(2)
        .exec(postNewAuthorAndThenDelete())
    }

  /*** Setup Load Simulation ***/
  setUp(
    scenarioBuilder.inject(
      nothingFor(5 seconds),
      rampUsers(userCount) during (rampDuration seconds))
  )
    .protocols(httpConf)
    .maxDuration(testDuration seconds)
      .assertions(
        global.responseTime.max.lt(300),
        global.successfulRequests.percent.gt(99)

      )

  /*** After ***/
  after {
    println("Stress test completed")
  }

}