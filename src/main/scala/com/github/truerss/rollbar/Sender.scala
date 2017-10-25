package com.github.truerss.rollbar

import scalaj.http._
import spray.json._
import java.util.logging

object Sender {
  import json._
  import entities.Result
  import logging.Logger

  private final val _timeout = 10000

  private final val hidden = Map("access_token" -> JsString("******"))

  private val log = Logger.getLogger(getClass.getName)

  private def uuid = java.util.UUID.randomUUID().toString

  def send(
           p: NotifyBuilder[Level],
           url: String = "https://api.rollbar.com/api/1/item/"
  )(implicit timeout: Int = _timeout) = {
    val u = uuid
    val json = p.payload.toJson
    val hideAccessToken = JsObject(json.asJsObject.fields ++ hidden)
    log.info(s"Send [$u]: $hideAccessToken to $url")
    val result = Http(url)
      .timeout(connTimeoutMs = _timeout, readTimeoutMs = _timeout)
      .postData(json.toString).asString.body.parseJson.convertTo[Result]
    log.info(s"Response [$u] given: $result")
    result
  }
}