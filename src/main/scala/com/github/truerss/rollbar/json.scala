package com.github.truerss.rollbar

import spray.json._

object json {
  import entities.{Trace => T, _}

  private implicit class StringExt(val x: String) extends AnyVal {
    def j: JsString = JsString(x)
  }

  private implicit class OptExt[T](val x: Option[T]) extends AnyVal {
    def j: JsValue = {
      x match {
        case Some(s: String) => s.j
        case Some(n: Int) => JsNumber(n)
        case Some(b: Boolean) => JsBoolean(b)
        case _ => JsNull
      }
    }
  }

  implicit object LogLevelWrites extends JsonWriter[LogLevel] {
    override def write(l: LogLevel) = JsString(l.name)
  }

  object JsonHelper {
    def conv(name: String, on: Option[String]): Map[String, JsString] = {
      on.map(m => Map(name -> JsString(m)))
        .getOrElse(Map.empty)
    }
  }

  implicit object BodyWrites extends JsonWriter[Body] {
    private type V = Map[String, JsValue]
    override def write(b: Body) = {
      val m: V = b.message match {
        case Some(message) =>
          message.additional.map { case _ @ (k, v) =>
            k -> JsString(v)
          } ++ Map("message" -> JsObject(Map("body" -> message.body.j)))

        case None =>
          Map.empty
      }

      val t: V = b.trace match {
        case Some(trace) =>
          Map("trace" -> TraceWrites.write(trace))
        case None =>
          Map.empty
      }

      val tc: V = if (b.trace_chain.isEmpty) {
        Map.empty
      } else {
        Map("trace_chain" -> JsArray(b.trace_chain.map(TraceWrites.write)))
      }

      JsObject(
        m ++ tc ++ t
      )
    }
  }

  implicit object DataWrites extends JsonWriter[Data] {
    override def write(d: Data) = {
      JsObject(
        "environment" -> d.environment.j,
        "body" -> BodyWrites.write(d.body),
        "level" -> LogLevelWrites.write(d.level.getOrElse(Info))
      )
    }
  }

  implicit object PayloadWrites extends JsonWriter[Payload] {
    override def write(p: Payload) = {
      JsObject(
        "access_token" -> p.accessToken.j,
        "data" -> DataWrites.write(p.data)
      )
    }
  }

  implicit object FrameContextWrites extends JsonWriter[FrameContext] {
    override def write(obj: FrameContext): JsValue = {
      JsObject(
        "pre" -> JsArray(obj.pre.map(_.j)),
        "post" -> JsArray(obj.post.map(_.j))
      )
    }
  }

  implicit object FrameWrites extends JsonWriter[Frame] {
    override def write(obj: Frame): JsValue = {
      JsObject(
        "filename" -> obj.fileName.j,
        "lineno" -> obj.lineNumber.j,
        "colno" -> obj.colNumber.j,
        "method" -> obj.method.j,
        "code" -> obj.code.j,
        "class_name" -> obj.className.j,
        "context" -> obj.context.map(FrameContextWrites.write).getOrElse(JsNull),
        "argspec" -> JsArray(obj.argSpec.map(_.j)),
        "varargspec" -> obj.varargSpec.j,
        "keywordspec" -> obj.keyWordSpec.j
      )
    }
  }

  implicit object RollBarException extends JsonWriter[RollBarException] {
    override def write(obj: RollBarException): JsValue = {
      JsObject(
        "class" -> obj.`class`.j,
        "message" -> obj.message.j,
        "description" -> obj.description.j
      )
    }
  }

  implicit object TraceWrites extends JsonWriter[T] {
    override def write(obj: T): JsValue = {
      JsObject(
        "frames" -> JsArray(obj.frames.map(FrameWrites.write)),
        "exception" -> RollBarException.write(obj.exception)
      )
    }
  }

  implicit object ResultReads extends JsonReader[Result] {
    override def read(p: JsValue) = {
      val f = p.asJsObject.fields
      f.get("err") match {
        case Some(JsNumber(err)) if err == 0 =>
          // success
          SuccessResult(
            err = 0,
            result =
              f.get("result").map(_.asJsObject)
                .flatMap(_.fields.get("uuid")).collect {
                case JsString(uuid) => uuid
              }.getOrElse("")
          )

        case Some(JsNumber(err)) =>
          // error
          FailureResult(
            err = err.toInt,
            message = f.get("message").collect {
              case JsString(m) => m
            }.getOrElse(s"Unexpected message from: $p")
          )

        case _ =>
          FailureResult(
            err = -1,
            message = s"Failed to parse json: $p"
          )
      }
    }
  }

}
