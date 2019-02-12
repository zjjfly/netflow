package io.netflow.storage.redis

import com.twitter.finagle.redis.util.StringToBuf
import io.netflow.flows.cflow._
import io.netflow.lib._
import io.wasted.util.Logger
import net.liftweb.json.Serialization

private[netflow] object NetFlowV9Packet
    extends FlowPacketMeta[NetFlowV9Packet]
    with Logger {
  def persist(fp: NetFlowV9Packet): Unit = fp.flows.foreach {
    case tmpl: NetFlowV9Template =>
      val id = tmpl.number
      debug(s"flowSet id:$id")
      val ip = tmpl.sender.getAddress.getHostAddress
      val key = StringToBuf("templates:" + ip)
      val index = StringToBuf(tmpl.id.toString)
      val value = Serialization.write(tmpl)
      Connection.client.hSet(key, index, StringToBuf(value))
    case _ =>
  }
}
