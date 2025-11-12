import sbt.*

object Dependencies {

  private val gatlingVersion: String = "3.13.5"

  val test: Seq[ModuleID] = Seq(
    "com.typesafe"          % "config"                    % "1.4.5"        % Test,
    "uk.gov.hmrc"          %% "performance-test-runner"   % "6.3.0"        % Test
  )
}
