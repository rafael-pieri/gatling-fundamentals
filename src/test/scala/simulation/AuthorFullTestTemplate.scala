package simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class AuthorFullTestTemplate extends Simulation {

  private val httpConf = http.baseUrl("http://localhost:8081")
    .header("Accept", "application/json")
    .proxy(Proxy("localhost", 8888))

  /*** HTTP CALLS ***/
  private def getAllAuthors = {
    exec(
      http("Get all authors")
        .get("/authors")
        .check(status.is(200))
    )
  }

  // add other calls here

  /** SCENARIO DESIGN */

  // using the http call, create a scenario that does the following:
  // 1. Get all authors
  // 2. Create new author
  // 3. Get details of that single
  // 4. Delete the author

  /** SETUP LOAD SIMULATION */

  // create a scenario that has runtime parameters for:
  // 1. Users
  // 2. Ramp up time
  // 3. Test duration

  /** Custom Feeder */

  // to generate the date for the Create new author JSON

  /** Helper methods */

  // for the custom feeder, or the defaults for the runtime parameters... and anything

  /** Variables */

  // for the helper methods

  /** Before & After */
  // to print out message at the start and end of the test
}