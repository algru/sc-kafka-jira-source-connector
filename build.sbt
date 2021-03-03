import Dependencies._

lazy val root = (project in file("."))
  .settings(
    organization := "algru",
    name := "sc-kafka-jira-source-connector",
    version := "0.1",
    scalaVersion := "2.13.5",
    assemblyJarName in assembly := "sc-kafka-jira-source-connector.jar",

    githubTokenSource := TokenSource.GitConfig("github.token"),
    resolvers += Resolver.githubPackages("algru"),

    libraryDependencies ++= Seq(
      scJiraClient,
      kafkaConnectApi,
      scalaConfig,
      logging,
      scalaTest
    ).flatten
)