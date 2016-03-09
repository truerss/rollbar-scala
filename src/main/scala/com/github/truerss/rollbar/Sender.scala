package com.github.truerss.rollbar

import scalaj.http._
import spray.json._

object Sender {
  import json._
  import entities.Result
  import java.util.logging.Logger

  def uuid = java.util.UUID.randomUUID().toString

  final val _timeout = 10000

  val log = Logger.getLogger(getClass.getName)
  def send(
           p: NotifyBuilder[Level],
           url: String = "https://api.rollbar.com/api/1/item/"
  )(implicit timeout: Int = _timeout) = {
    val u = uuid
    val json = p.payload.toJson.toString
    log.info(s"Send [$u]: $json to $url")
    val result = Http(url)
      .timeout(connTimeoutMs = _timeout, readTimeoutMs = _timeout)
      .postData(json).asString.body.parseJson.convertTo[Result]
    log.info(s"Response [$u] given: $result")
    result
  }
}