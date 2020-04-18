package simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class RuntimeParameters extends Simulation {

  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  def userCount: Int = getProperty("USERS", "5").toInt
  def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt
  def testDuration: Int = getProperty("DURATION", "60").toInt

  before {
    println(s"Running test with ${userCount} users")
    println(s"Ramping users over ${rampDuration} seconds")
    println(s"Total test duration: ${testDuration} seconds")
  }

  private val httpConf = http.baseUrl("http://localhost:8081")
    .header("Accept", "application/json")

  private def getAllAuthors = {
    exec(
      http("Get all authors")
        .get("/authors")
        .check(status.is(200))
    )
  }

  private val scenarioBuilder = scenario("Get all authors")
    .forever() {
      exec(getAllAuthors)
    }

  setUp(
    scenarioBuilder.inject(
      nothingFor(5 seconds),
      rampUsers(userCount) during (rampDuration second)
    )
  ).protocols(httpConf)
    .maxDuration(testDuration seconds)

}