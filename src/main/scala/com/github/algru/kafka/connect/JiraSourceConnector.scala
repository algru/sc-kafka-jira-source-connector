package com.github.algru.kafka.connect

import com.github.algru.kafka.connect.config.LibraryInfoConfig
import com.github.algru.kafka.connect.traits.Logging
import com.github.algru.kafka.connect.types.KafkaConnectTypes.{Props, TasksConfigsList, emptyProps, emptyTasksConfigsList}
import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.source.SourceConnector

class JiraSourceConnector extends SourceConnector with Logging {
  private var configProps: Props = _

  override def start(props: Props): Unit = {
    configProps = props
  }

  override def taskClass(): Class[_ <: Task] = classOf[JiraSourceTask]

  override def taskConfigs(maxTasks: Int): TasksConfigsList = {
    if (maxTasks > 1) {
      log.info("Ignore maxTask parameter because we need only one task")
    }
    initTasksConfigs
  }

  private def initTasksConfigs: TasksConfigsList = {
    val tasksConfigs = emptyTasksConfigsList
    val taskProps = emptyProps
    taskProps.putAll(configProps)
    tasksConfigs.add(taskProps)
    tasksConfigs
  }

  override def stop(): Unit = {}

  override def config(): ConfigDef = configDef

  override def version(): String = LibraryInfoConfig.getVersion

  private val configDef: ConfigDef = new ConfigDef()
    .define(
      "address",
      ConfigDef.Type.STRING,
      ConfigDef.Importance.HIGH,
      "Jira server address")
    .define(
      "username",
      ConfigDef.Type.STRING,
      ConfigDef.Importance.HIGH,
      "Jira username")
    .define(
      "password",
      ConfigDef.Type.STRING,
      ConfigDef.Importance.HIGH,
      "Jira password")
    .define(
      "topic",
      ConfigDef.Type.STRING,
      ConfigDef.Importance.HIGH,
      "Name of kafka topic")
    .define(
      "offset-issue-change-time",
      ConfigDef.Type.STRING,
      "",
      ConfigDef.Importance.HIGH,
      "From what date should connector start retrieving issues (format: yyyy-MM-dd'T'HH:mm:ss.SSSSSSSS, example: 2021-03-03T02:11:36.64071563)")
    .define(
      "poll-interval",
      ConfigDef.Type.INT,
      10000,
      ConfigDef.Importance.HIGH,
      "Poll interval in ms (default value = 10000ms)")
}
