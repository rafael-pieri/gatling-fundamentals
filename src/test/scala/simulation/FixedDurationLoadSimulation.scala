package simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class FixedDurationLoadSimulation extends Simulation {

  private val httpConf = http.baseUrl("http://localhost:8081")
    .header("Accept", "application/json")

  private def getAllAuthors = {
    exec(
      http("Get all authors")
        .get("/authors")
        .check(status.is(200))
    )
  }

  private def getSpecificAuthor = {
    exec(
      http("Get specific author")
        .get("/authors/8caaca39-844d-43f1-8865-31c2fa0f94ba")
        .check(status.is(200))
    )
  }

  private val scenarioBuilder = scenario("Fixed Duration Load Simulation")
    .forever() {
      exec(getAllAuthors)
        .pause(5)
        .exec(getSpecificAuthor)
        .pause(5)
        .exec(getAllAuthors)
    }

  setUp(
    scenarioBuilder.inject(
      nothingFor(5 seconds),
      atOnceUsers(10),
      rampUsers(50) during (30 second)
    ).protocols(httpConf.inferHtmlResources())
  ).maxDuration(1 minute)

}