# cds-reimbursement-claim-performance-tests
Performance test suite for the CDS Reimbursements services, using [performance-test-runner](https://github.com/hmrc/performance-test-runner) under the hood.

## Pre-requisites

### Services

Prior to executing the tests ensure you have:

* Docker - to start mongo container
* Installed/configured service manager

Run the following commands to start the services locally:
```
docker run --rm -d --name mongo -d -p 27017:27017 mongo:3.6

sm2 -start CDSRC_ALL
```

### Logging

The template uses [logback.xml](src/test/resources) to configure log levels. The default log level is *WARN*. This can be updated to use a lower level for example *TRACE* to view the requests sent and responses received during the test.

### WARNING :warning:

Do **NOT** run a full performance test against staging from your local machine. Execute the performance test job from the dashboard in [Performance Jenkins](https://performance.tools.staging.tax.service.gov.uk/job/Borders%20and%20Trade%20Live%20Services/job/CDS%20Reimbursements/job/cds-reimbursement-claim-performance-tests/).

## Tests

It might be useful to try the journey with one user to check that everything works fine before running the full performance test. Run smoke test (locally) as follows:

```
sbt -Dperftest.runSmokeTest=true -DrunLocal=true  Gatling/test
```

Run full performance test (locally) as follows:

```
sbt -DrunLocal=true  Gatling/test
```

Run smoke test (staging) as follows:

```
sbt -Dperftest.runSmokeTest=true -DrunLocal=false Gatling/test
```

### Scalafmt

This repository uses [Scalafmt](https://scalameta.org/scalafmt/), a code formatter for Scala. The formatting rules configured for this repository are defined within [.scalafmt.conf](.scalafmt.conf).

To apply formatting to this repository using the configured rules in [.scalafmt.conf](.scalafmt.conf) execute:

```
sbt scalafmtAll
```

To check files have been formatted as expected execute:

```
sbt scalafmtCheckAll scalafmtSbtCheck
```

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
