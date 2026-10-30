# cds-reimbursement-claim-performance-tests

## About
Performance test suite for the CDS Reimbursements services, using [performance-test-runner](https://github.com/hmrc/performance-test-runner) under the hood.
- [cds-reimbursement-claim](https://catalogue.tax.service.gov.uk/repositories/cds-reimbursement-claim)
- [cds-reimbursement-claim-frontend](https://catalogue.tax.service.gov.uk/repositories/cds-reimbursement-claim-frontend)

The performance tests can be run both locally and on Jenkins.
## Key Information
Do **NOT** run a full performance test against staging from your local machine.

Instead, [set up a performance test job in Jenkins](https://docs.tax.service.gov.uk/mdtp-handbook/documentation/mdtp-test-approach/performance-testing/performance-test-a-microservice/index.html#set-up-a-build-on-jenkins-to-run-against-the-staging-environment) and run the job from the [Performance Jenkins dashboard](https://performance.tools.staging.tax.service.gov.uk/job/Borders%20and%20Trade%20Live%20Services/job/CDS%20Reimbursements/job/cds-reimbursement-claim-performance-tests/).

Only run smoke tests locally. Full performance tests can place a significant load on your machine and may cause performance issues.

# Pre-requisites

Make sure the following tools:technologies and configurations are in place before running tests:

| **Technology**             | **Description**                                                                           |
|----------------------------|-------------------------------------------------------------------------------------------|
| **Java (JDK 11 or 21)**    | Required to compile and run Scala-based projects                                          |
| **sbt (Scala Build Tool)** | To compile and run the Play application & execute tests                                   |
| **Scala**                  | Required to compile & run the application. Ensure the version matches what's in build.sbt |
| **Service Manager (sm2)**  | See [service manager](https://github.com/hmrc/service-manager) To start services locally                                      |
| **Docker**                 | Makes it easier to run certain software tools locally without having to install them      |
| **Mongo DB**               | Set up [Mongo DB](https://www.mongodb.com/docs/manual/installation/) using Docker                                                       |
| **Git**                    | To clone the repository and manage branches                                               | 


### Start services locally
If you're executing the test scripts locally:
- Ensure all required services are up and running
- Start the service manager to launch all dependencies (Start all services)
- Use the following commands to start services locally

| **Command**                                   | **Description**                                                                                           |
|-----------------------------------------------|-----------------------------------------------------------------------------------------------------------|
| `sm2 --start CDSRC_ALL`                       | Start all services                                                                                        |
| `sm2 --start CDSRC_FRONTEND`                  | Start a particular service                                                                                |
| `sm2 --start SERVICE_1 SERVICE_2`             | Start multiple services                                                                                   |
| `sm2 --stop-all`                              | Stop all services                                                                                         |
| `sm2 --stop CDSRC_FRONTEND`                   | Stop a particular service                                                                                 |
| `sm2 --restart CDSRC_ALL`                     | Restart all services                                                                                      |
| `sm2 --restart CDSRC_FRONTEND`                | Restart particular service                                                                                |
| `sm2 --restart SERVICE_NAME -latest`          | When restarting a service or profile,you can specify -latest to check for a new version before restarting |                                              
| `sm2 --s (or) sm2 --status`                   | Checking the status of services (-s or -status)                                                           |
| `sm2 --list`                                  | List all available services                                                                               |


## Testing Locally
Follow the steps below to run the performance tests locally.

### How to Run the Smoke Tests
It might be useful to try the journey with one user to check that everything works fine before running the full performance test. Run smoke test (locally) as follows:

```bash
sbt -Dperftest.runSmokeTest=true -DrunLocal=true  Gatling/test
```

Run full performance test (locally) as follows:

```bash
sbt -DrunLocal=true  Gatling/test
```

Run smoke test (staging) as follows:

```bash
sbt -Dperftest.runSmokeTest=true -DrunLocal=false Gatling/test
```

## Post-Merge Testing
>**Important:** The full performance tests run all journeys at a configurable percentage of load, where 100% simulates the expected SA peak load. Parameters such as the number of parallel runs, ramp-up time, constant rate time, and ramp-down time can also be configured.
## Jenkins Builds
[cds-reimbursement-claim-performance-tests](https://performance.tools.staging.tax.service.gov.uk/job/Borders%20and%20Trade%20Live%20Services/job/CDS%20Reimbursements/job/cds-reimbursement-claim-performance-tests/)

### Logging

The template uses [logback.xml](src/test/resources) to configure log levels. The default log level is *WARN*. This can be updated to use a lower level for example *TRACE* to view the requests sent and responses received during the test.

## Scalafmt

Check all project files are formatted as expected as follows:

```bash
sbt scalafmtCheckAll scalafmtCheck
```

Format `*.sbt` and `project/*.scala` files as follows:

```bash
sbt scalafmtSbt
```

Format all project files as follows:

```bash
sbt scalafmtAll
```

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
