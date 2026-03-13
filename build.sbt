lazy val root = (project in file("."))
  .enablePlugins(GatlingPlugin)
  .settings(
    name := "cds-reimbursement-claim-performance-tests",
    version := "3.3.6",
    isPublicArtefact := true,
    scalaVersion := "3.3.6",
    scalacOptions ++= Seq("-feature", "-language:implicitConversions", "-language:postfixOps"),
    Test / testOptions := Seq.empty,
    libraryDependencies ++= Dependencies.test

  )
  .settings(ThisBuild / useSuperShell := false)
