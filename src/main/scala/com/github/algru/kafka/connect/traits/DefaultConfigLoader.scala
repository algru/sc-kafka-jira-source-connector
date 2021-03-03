package com.github.algru.kafka.connect.traits

import com.typesafe.config.{Config, ConfigFactory}

trait DefaultConfigLoader {
  val config: Config = ConfigFactory.load()
}
