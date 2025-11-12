lazy val root = (project in file("."))
  .enablePlugins(GatlingPlugin)
 // .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    name := "cds-reimbursement-claim-performance-tests",
    version := "3.3.6",
    isPublicArtefact := true,
    crossScalaVersions := Seq("2.13.16", "3.3.6"),
    scalaVersion := crossScalaVersions.value.head,
    scalacOptions ++= Seq("-feature", "-language:implicitConversions", "-language:postfixOps"),
    Test / testOptions := Seq.empty,
    libraryDependencies ++= Dependencies.test

    )
  .settings(ThisBuild / useSuperShell := false)
