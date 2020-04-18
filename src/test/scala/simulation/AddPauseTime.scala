package simulation

import io.gatling.core.Predef.{atOnceUsers, scenario, _}
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._

class AddPauseTime extends Simulation {

  private val httpConf = http.baseUrl("http://localhost:8081")
    .header("Accept", "application/json")

  private val scenarioBuilder = scenario("Author calls")
    .exec(http("Get all authors - 1st call")
      .get("/authors"))
    .pause(5)
    .exec(http("Get specific author")
      .get("/authors/8caaca39-844d-43f1-8865-31c2fa0f94ba"))
    .pause(1, 20)
    .exec(http("Get all authors - 2nd call")
      .get("/authors"))
    .pause(30)

  setUp(
    scenarioBuilder.inject(atOnceUsers(1))
  ).protocols(httpConf)

}