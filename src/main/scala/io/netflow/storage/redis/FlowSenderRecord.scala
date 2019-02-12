package io.netflow
package storage
package redis

import java.net.InetAddress

import com.twitter.finagle.redis.util.{ BufToString, StringToBuf }
import com.twitter.util.Future
import io.netflow.lib._

private[netflow] object FlowSenderRecord extends FlowSenderMeta {
  def findAll(): Future[List[FlowSender]] = {
    Connection.client.sMembers(StringToBuf("senders")).map(_.map(BufToString(_))).flatMap { senders =>
      Future.collect(senders.map { sender =>
        val key = "prefixes:" + sender
        Connection.client.sMembers(StringToBuf(key)).map(_.map(BufToString(_)))
          .map(_.flatMap(string2prefix)).map { addrs =>
            storage.FlowSender(InetAddress.getByName(sender), None, addrs)
          }
      }.toSeq).map(_.toList)
    }
  }

  def find(inet: InetAddress): Future[FlowSender] = {
    val key = "prefixes:" + inet.getHostAddress
    Connection.client.sMembers(StringToBuf(key)).map(_.map(BufToString(_)))
      .map(_.flatMap(string2prefix)).map { addrs =>
        storage.FlowSender(inet, None, addrs)
      }
  }

  def save(sender: FlowSender): Future[FlowSender] = {
    val pfxs = sender.prefixes.map(p => StringToBuf(p.toString)).toList
    Connection.client.sAdd(StringToBuf("senders"), StringToBuf(sender.ip.getHostAddress) :: Nil).flatMap {
      senderAdded =>
        Connection.client.sAdd(StringToBuf("prefixes:" + sender.ip.getHostAddress), pfxs).map {
          pfxsAdded => sender
        }
    }
  }

  def delete(inet: InetAddress): Future[Unit] = {
    Connection.client.dels(StringToBuf("prefixes:" + inet.getHostAddress) :: Nil).flatMap { pfxsDeleted =>
      Connection.client.sRem(StringToBuf("senders"), StringToBuf(inet.getHostAddress) :: Nil).map {
        done => ()
      }
    }
  }
}
