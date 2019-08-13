package io.netflow.storage.redis

import java.net.InetAddress

import com.twitter.finagle.redis.util.{BufToString, StringToBuf}
import com.twitter.util.Future
import io.netflow.flows.cflow._
import io.netflow.lib._
import net.liftweb.json.JsonParser

private[netflow] object NetFlowV9TemplateRecord
    extends NetFlowTemplateMeta[NetFlowV9Template] {
  def findAll(inet: InetAddress): Future[Seq[NetFlowV9Template]] = {
    val key = StringToBuf("templates:" + inet.getHostAddress)
    Connection.client.hGetAll(key).map {
      _.map(t => BufToString(t._2))
        .map(JsonParser.parse)
        .flatMap(_.extractOpt[NetFlowV9Template])
    }
  }
}
