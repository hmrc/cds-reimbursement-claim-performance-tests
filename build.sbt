lazy val root = (project in file("."))
  .enablePlugins(GatlingPlugin)
  .enablePlugins(SbtAutoBuildPlugin)
  .settings(
    name := "cds-reimbursement-claim-performance-tests",
    version := "0.1.0",
    scalaVersion := "2.12.13",
    //implicitConversions & postfixOps are Gatling recommended -language settings
    scalacOptions ++= Seq("-feature", "-language:implicitConversions", "-language:postfixOps"),
    // Enabling sbt-auto-build plugin provides DefaultBuildSettings with default `testOptions` from `sbt-settings` plugin.
    // These testOptions are not compatible with `sbt gatling:test`. So we have to override testOptions here.
    testOptions in Test := Seq.empty,
    libraryDependencies ++= Dependencies.test
  )
