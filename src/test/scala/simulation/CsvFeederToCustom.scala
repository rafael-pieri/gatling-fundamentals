package simulation

import io.gatling.core.Predef.{atOnceUsers, scenario}
import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CsvFeederToCustom extends Simulation {

  private val httpConf = http.baseUrl("http://localhost:8081")
    .header("Accept", "application/json")

  private val ids = List("8caaca39-844d-43f1-8865-31c2fa0f94ba", "efc2c81b-d686-49b3-9751-b509046c8e04", "5670f449-7b96-4d62-ac11-b10633c451e4").iterator

  private val customFeeder = Iterator.continually(Map("id" -> ids.next()))

  private def getSpecificAuthor = {
    repeat(3) {
      feed(customFeeder)
        .exec(http("Get specific author")
          .get("/authors/${id}")
          .check(status.is(200))
          .check(bodyString.saveAs("responseBody")))
        .pause(1)
        .exec { session => println(session("responseBody").as[String]); session }
    }
  }

  private val scenarioBuilder = scenario("Csv Feeder test")
    .exec(getSpecificAuthor)

  setUp(
    scenarioBuilder.inject(atOnceUsers(1))
  ).protocols(httpConf)

}