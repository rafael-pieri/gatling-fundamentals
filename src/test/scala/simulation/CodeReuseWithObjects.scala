package simulation

import io.gatling.core.Predef.{atOnceUsers, scenario}
import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CodeReuseWithObjects extends Simulation {

  private val httpConf = http.baseUrl("http://localhost:8081")
    .header("Accept", "application/json")


  private def getAllAuthors = {
    repeat(3) {
      exec(http("Get all authors - 1st call")
        .get("/authors")
        .check(status.is(200)))
    }
  }

  private def getSpecificAuthor = {
    repeat(5) {
      exec(http("Get specific author")
        .get("/authors/8caaca39-844d-43f1-8865-31c2fa0f94ba")
        .check(status.in(200 to 210)))
    }
  }

  private val scenarioBuilder = scenario("Code reuse")
    .exec(getAllAuthors)
    .pause(5)
    .exec(getSpecificAuthor)
    .pause(5)
    .exec(getAllAuthors)

  setUp(
    scenarioBuilder.inject(atOnceUsers(1))
  ).protocols(httpConf)

}