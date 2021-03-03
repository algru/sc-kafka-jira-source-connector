import sbt._

object Dependencies {
  lazy val scJiraClient = Seq(
    "algru" % "sc-jira-client_2.13" % "0.2"
  )

  lazy val kafkaConnectApi = Seq(
    "org.apache.kafka" % "connect-api" % "2.7.0" % "provided"
  )

  lazy val scalaConfig = Seq(
    "com.typesafe" % "config" % "1.4.0"

  )

  lazy val logging = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

  lazy val scalaTest = Seq(
    "org.scalatest" %% "scalatest" % "3.2.5" % Test,
    "org.scalamock" %% "scalamock" % "5.1.0" % Test
  )
}
