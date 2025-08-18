import sbt.*

object Dependencies {

  private val gatlingVersion: String = "3.13.5"

  val test: Seq[ModuleID] = Seq(
    "com.typesafe"          % "config"                    % "1.4.4"        % Test,
    "uk.gov.hmrc"          %% "performance-test-runner"   % "6.2.0"        % Test,
    "io.gatling"            % "gatling-test-framework"    % gatlingVersion % Test,
    "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % Test
  )
}
