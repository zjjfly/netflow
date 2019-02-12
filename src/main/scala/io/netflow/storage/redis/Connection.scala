package io.netflow.storage.redis

import com.twitter.finagle.redis.Client
import com.twitter.finagle.redis.util.StringToBuf
import io.netflow.lib.NodeConfig
import io.netflow.storage.{ Connection => ConnectionMeta }

private[storage] object Connection extends ConnectionMeta {
  lazy val client = Client(NodeConfig.values.redis.hosts.mkString(","))
//  client.auth(StringToBuf("jjzi"))
  def start(): Unit = client
  def shutdown(): Unit = ()
}
