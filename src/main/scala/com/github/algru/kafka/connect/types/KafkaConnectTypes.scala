package com.github.algru.kafka.connect.types

import org.apache.kafka.connect.source.SourceRecord

import java.util

object KafkaConnectTypes {
  type Props = util.Map[String, String]
  type TasksConfigsList = util.List[Props]
  type SourceRecordsList = util.List[SourceRecord]

  def emptyProps = new util.HashMap[String, String]()
  def emptyTasksConfigsList = new util.ArrayList[Props]()
  def emptySourceRecordsList = new util.ArrayList[SourceRecord]()
}
