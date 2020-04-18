# Gatling

Some examples of load testing using Gatling.
See documentation at: https://gatling.io/docs/current/quickstart

## Overview
All tests created were based on the service APIs found in this repository: https://github.com/rafael-pieri/api-rest-algaworks

## How to execute load tests
In order to run the tests, execute the following command:

```mvn gatling:test -Dgatling.simulationClass=simulation.${simulation-class-name}```
