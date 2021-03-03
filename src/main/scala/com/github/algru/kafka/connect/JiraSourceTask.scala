package com.github.algru.kafka.connect

import com.github.algru.jira.client.JiraClient
import com.github.algru.jira.client.model.AbsenceIssue
import com.github.algru.kafka.connect.config.LibraryInfoConfig
import com.github.algru.kafka.connect.traits.Logging
import com.github.algru.kafka.connect.types.KafkaConnectTypes.{Props, emptyProps, emptySourceRecordsList}
import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.source.{SourceRecord, SourceTask}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util
import java.util.Collections
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.jdk.CollectionConverters.SeqHasAsJava

case class Issue(body: String, changeTime: String)

class JiraSourceTask extends SourceTask with Logging {
  private var jiraClient: JiraClient = _
  private var topic: String = _
  private var jiraAddress: String = _
  private var jiraUsername: String = _
  private var jiraPassword: String = _
  private var pollInterval: Int = _
  private var justStarted: Boolean = true
  private var offsetIssueChangeTime: String = _

  private val sourcePartition: Props = emptyProps
  private val emptyPoll = emptySourceRecordsList

  override def start(props: Props): Unit = {
    topic = props.get("topic")
    jiraAddress = props.get("address")
    jiraUsername = props.get("username")
    jiraPassword = props.get("password")
    pollInterval = Integer.parseInt(props.get("poll-interval"))
    offsetIssueChangeTime = props.getOrDefault("offset-issue-change-time", LocalDateTime.MIN.format(DateTimeFormatter.ISO_DATE_TIME))
    sourcePartition.put("source", s"sc-jira-$topic")
    initOffsets()

    jiraClient = JiraClient(jiraAddress, jiraUsername, jiraPassword)

    log.info(s"Start polling JIRA ABSENCE issues to topic $topic with offset $offsetIssueChangeTime")
  }

  override def poll(): util.List[SourceRecord] = {
    if (!tryWait) {
      return emptyPoll
    }
    val startPeriod = LocalDateTime.parse(offsetIssueChangeTime, DateTimeFormatter.ISO_DATE_TIME)
    val endPeriod = LocalDateTime.now
    offsetIssueChangeTime = endPeriod.format(DateTimeFormatter.ISO_DATE_TIME)
    val absences: Seq[AbsenceIssue] = retrieveLastJiraIssues(startPeriod, endPeriod)
    if (absences.nonEmpty) {
      val records = absences.map(absence => new SourceRecord(
        sourcePartition,
        Collections.singletonMap("offset-issue-change-time", absence.lastUpdate.format(DateTimeFormatter.ISO_DATE_TIME)),
        topic,
        Schema.STRING_SCHEMA,
        absence.toString
      ))
      log.debug(s"Polled absences count: ${absences.length}. Last absence change: $offsetIssueChangeTime")

      records.asJava
    } else {
      emptyPoll
    }
  }

  override def stop(): Unit = {}

  override def version(): String = LibraryInfoConfig.getVersion

  private def initOffsets(): Unit = {
    val offset = context.offsetStorageReader().offset(sourcePartition)
    if (offset != null) {
      offsetIssueChangeTime = offset.getOrDefault("offset-issue-change-time", "").asInstanceOf[String]
    } else {
      offsetIssueChangeTime = LocalDateTime.now.format(DateTimeFormatter.ISO_DATE_TIME)
    }
  }

  private def retrieveLastJiraIssues(start: LocalDateTime, end: LocalDateTime): Seq[AbsenceIssue] = {
    Await.result(jiraClient.getAbsences(start, end), Duration.Inf)
  }

  private def tryWait: Boolean = {
    if (!justStarted && pollInterval > 0) {
      try {
        Thread.sleep(pollInterval)
      } catch {
        case e: InterruptedException =>
          log.error(s"Poll thread was interrupted while sleep. Skip poll.", e)
          Thread.interrupted()
          return false
      }
    } else {
      justStarted = false
    }
    true
  }
}
