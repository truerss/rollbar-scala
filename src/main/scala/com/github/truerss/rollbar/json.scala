package com.github.truerss.rollbar

import spray.json._

object json {
  import entities._

  implicit object LogLevelWrites extends JsonWriter[LogLevel] {
    override def write(l: LogLevel) = JsString(l.name)
  }

  object JsonHelper {
    def conv(name: String, on: Option[String]): Map[String, JsString] = {
      on.map(m => Map(name -> JsString(m)))
        .getOrElse(Map.empty)
    }
  }

  implicit object NotifyMessageWrites extends JsonWriter[NotifyMessage] {
    override def write(n: NotifyMessage) = {
      val additional = n.additional.map { case _ @ (k, v) =>
        k -> JsString(v)
      }
      JsObject(
        Map("message" -> JsObject(Map("body" -> JsString(n.body)) ++ additional))
      )
    }
  }

  implicit object BodyWrites extends JsonWriter[Body] {
    override def write(b: Body) = {
      JsObject(NotifyMessageWrites.write(b.message).fields)
    }
  }

  implicit object DataWrites extends JsonWriter[Data] {
    override def write(d: Data) = {
      JsObject(
        "environment" -> JsString(d.environment),
        "body" -> BodyWrites.write(d.body),
        "level" -> LogLevelWrites.write(d.level.getOrElse(Info))
      )
    }
  }

  implicit object PayloadWrites extends JsonWriter[Payload] {
    override def write(p: Payload) = {
      JsObject(
        "access_token" -> JsString(p.accessToken),
        "data" -> DataWrites.write(p.data)
      )
    }
  }

  implicit object ResultReads extends JsonReader[Result] {
    override def read(p: JsValue) = {
      p.asJsObject.getFields("err", "result") match {
        case Seq(JsNumber(err), JsObject(result)) =>
          result.get("uuid").map { uuid => Result(err.toInt, uuid.toString()) }
            .getOrElse(deserializationError(s"Unexpected json value: $p") )

        case _ => deserializationError(s"Unexpected json value: $p")
      }
    }
  }

}
