package simulation

import io.gatling.core.Predef.{atOnceUsers, scenario, _}
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._

class CheckResponseBodyAndExtract extends Simulation {

  private val httpConf = http.baseUrl("http://localhost:8081")
    .header("Accept", "application/json")

  private val scenarioBuilder = scenario("Check JSON Path")
    .exec(http("Get specific author")
      .get("/authors/8caaca39-844d-43f1-8865-31c2fa0f94ba")
      .check(jsonPath("$.name").is("Machado de Assis")))

    .exec(http("Get all authors")
      .get("/authors")
      .check(jsonPath("$[1].id").saveAs("authorId")))
    .exec { session => println(session); session }

    .exec(http("Get specific author")
      .get("/authors/${authorId}")
      .check(jsonPath("$.name").is("Ariano Suassuna"))
      .check(bodyString.saveAs("responseBody")))
    .exec { session => println(session("responseBody").as[String]); session }

  setUp(
    scenarioBuilder.inject(atOnceUsers(1))
  ).protocols(httpConf)

}