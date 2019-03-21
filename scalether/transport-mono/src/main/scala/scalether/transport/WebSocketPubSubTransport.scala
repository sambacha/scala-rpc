package scalether.transport

import java.net.URI

import com.fasterxml.jackson.databind.JsonNode
import io.daonomic.rpc.domain.Request
import io.daonomic.rpc.mono.SimpleWebSocketClient
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.web.reactive.socket.adapter.NettyWebSocketSessionSupport
import reactor.core.publisher.Flux
import scalether.core.{Ethereum, PubSubTransport}

/**
  * Simple WebSocket transport for ethereum node. opens new web socket connection for every subscription
  * doesn't reconnect on error
  */
class WebSocketPubSubTransport(uri: String) extends PubSubTransport {
  private val mapper = Ethereum.mapper

  override def subscribe[T: Manifest](name: String, param: Option[Any]): Flux[T] = {
    val subscribe = mapper.writeValueAsString(new Request(1, "eth_subscribe", List(name) ++ param))
    SimpleWebSocketClient.connect(new URI(uri), NettyWebSocketSessionSupport.DEFAULT_FRAME_MAX_SIZE * 2, Flux.just[String](subscribe).concatWith(Flux.never()))
      .map[JsonNode](m => mapper.readTree(m))
      .filter(m => m.path("params").path("result").isObject)
      .map(m => mapper.convertValue[T](m.path("params").path("result")))
  }
}

object WebSocketPubSubTransport {
  val logger: Logger = LoggerFactory.getLogger(WebSocketPubSubTransport.getClass)
}