package simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CustomFeeder extends Simulation {

  private val httpConf = http.baseUrl("http://localhost:8081")
    .header("Accept", "application/json")

  /*** Custom Feeder ***/
  private val customFeeder = Iterator.continually(Map(
    "name" -> "Manoel Bandeira",
    "nationality" -> "Brasileira"
  ))

  private def postNewAuthor() = {
    repeat(5) {
      feed(customFeeder)
        .exec(http("Post new author")
          .post("/authors")
            .body(ElFileBody("request-bodies/NewAuthorTemplate.json")).asJson
          .check(status.is(201)))
        .pause(1)
    }
  }

  private val scenarioBuilder = scenario("Post new authors")
      .exec(postNewAuthor())

  setUp(
    scenarioBuilder.inject(atOnceUsers(1))
  ).protocols(httpConf)

}