package simulation

import io.gatling.core.Predef.{atOnceUsers, scenario}
import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CsvFeeder extends Simulation {

  private val httpConf = http.baseUrl("http://localhost:8081")
    .header("Accept", "application/json")

  private val csvFeeder = csv("data/authorCsvFile.csv").circular

  private def getSpecificAuthor = {
    repeat(10) {
      feed(csvFeeder)
        .exec(http("Get specific author")
          .get("/authors/${id}")
          .check(jsonPath("$.name").is("${name}"))
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