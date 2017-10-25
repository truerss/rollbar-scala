package com.github.truerss.rollbar

import com.github.truerss.rollbar.entities.Payload
import spray.json.JsonWriter

private[rollbar] sealed trait Step
private[rollbar] trait Empty extends Step
private[rollbar] trait Level extends Step

class NotifyBuilder[T <: Step] private (val accessToken: String,
                                        val environment: String = "test") {
  val payload: entities.Payload = null
  protected val url = "https://api.rollbar.com/api/1/item/"

  def info(message: String, fields: Map[String, String] = Map.empty) = {
    new NotifyBuilder[Level](accessToken, environment) {
      override val payload =
        NotifyBuilder.build(accessToken, entities.Info,
          environment, message, fields)
    }
  }
  def warning(message: String, fields: Map[String, String] = Map.empty) = {
    new NotifyBuilder[Level](accessToken, environment) {
      override val payload =
        NotifyBuilder.build(accessToken, entities.Warning,
          environment, message, fields)
    }
  }
  def debug(message: String, fields: Map[String, String] = Map.empty) = {
    new NotifyBuilder[Level](accessToken, environment) {
      override val payload =
        NotifyBuilder.build(accessToken, entities.Debug,
          environment, message, fields)
    }
  }
  def critical(message: String, fields: Map[String, String] = Map.empty) = {
    new NotifyBuilder[Level](accessToken, environment) {
      override val payload =
        NotifyBuilder.build(accessToken, entities.Critical,
          environment, message, fields)
    }
  }
  def error(message: String, fields: Map[String, String] = Map.empty) = {
    new NotifyBuilder[Level](accessToken, environment) {
      override val payload =
        NotifyBuilder.build(accessToken, entities.Error,
          environment, message, fields)
    }
  }

  def toJson()(implicit writer: JsonWriter[Payload], t: T =:= Level) =
    writer.write(payload)

}

object NotifyBuilder {
  import entities._

  def apply(accessToken: String, environment: String) =
    new NotifyBuilder[Empty](accessToken, environment)

  private def build(at: String, lvl: LogLevel, env: String,
                    message: String, fields: Map[String, String] = Map.empty) = {
    val m  = NotifyMessage(message, fields)
    val body = Body(message = m)
    val data = Data(env, body, level = Some(lvl))
    Payload(at, data)
  }
}
