package com.github.algru.kafka.connect.config

import com.github.algru.kafka.connect.traits.DefaultConfigLoader

object LibraryInfoConfig extends DefaultConfigLoader {
  def getVersion: String = config.getString("connector.version")
}
