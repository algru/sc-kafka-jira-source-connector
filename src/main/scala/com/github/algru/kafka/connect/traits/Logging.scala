package com.github.algru.kafka.connect.traits

import org.slf4j.{Logger, LoggerFactory}

trait Logging {
  val log: Logger = LoggerFactory.getLogger(this.getClass)
}
