package simulation

import io.gatling.core.Predef.{atOnceUsers, scenario}
import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicLoadSimulation extends Simulation {

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

  private val scenarioBuilder = scenario("Basic Load Simulation")
    .exec(getAllAuthors)
    .pause(5)
    .exec(getSpecificAuthor)
    .pause(5)
    .exec(getAllAuthors)

  private val secondScenarioBuilder = scenario("Second Basic Load Simulation")
    .exec(getAllAuthors)
    .pause(5)
    .exec(getSpecificAuthor)
    .pause(5)
    .exec(getAllAuthors)

  setUp(
    scenarioBuilder.inject(
      nothingFor(5),
      atOnceUsers(5),
      rampUsers(10) during (10)
    ).protocols(httpConf.inferHtmlResources()),
    secondScenarioBuilder.inject(
      atOnceUsers(500)
    ).protocols(httpConf)
  )

}