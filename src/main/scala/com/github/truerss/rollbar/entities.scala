package com.github.truerss.rollbar


object entities {
  sealed trait LogLevel { val name: String }
  case object Critical extends LogLevel {
    override val name = "critical"
  }
  case object Error extends LogLevel {
    override val name = "error"
  }
  case object Warning extends LogLevel {
    override val name = "warning"
  }
  case object Info extends LogLevel {
    override val name = "info"
  }
  case object Debug extends LogLevel {
    override val name = "debug"
  }

  case object LogLevel {
    def from(l: String) = {
      l.toLowerCase match {
        case Critical.name => Critical
        case Error.name => Error
        case Warning.name => Warning
        case Info.name => Info
        case Debug.name => Debug
        case _ => Info
      }
    }
  }

  case class Payload(accessToken: String, data: Data)

  case class Data(
    environment: String,
    body: Body,
    level: Option[LogLevel] = Some(Info)
  )

  case class Trace(frames: Vector[Frame], exception: RollBarException)
  case class Frame(
                  fileName: String,
                  lineNumber: Option[Int],
                  colNumber: Option[Int],
                  method: Option[String],
                  code: Option[String],
                  className: Option[String],
                  context: Option[FrameContext],
                  argSpec: Vector[String],
                  varargSpec: Option[String],
                  keyWordSpec: Option[String]
                  )
  case class FrameContext(
                         pre: Vector[String],
                         post: Vector[String]
                         )
  case class RollBarException(
                              `class`: String,
                              message: Option[String],
                              description: Option[String]
                             )
  object RollBarException {
    def apply(t: Throwable): RollBarException = {
      RollBarException(
        `class` = t.getClass.getCanonicalName,
        message = Option(t.getMessage),
        description = None
      )
    }
  }

  case class Body(message: Option[NotifyMessage],
                  trace: Option[Trace] = None,
                  trace_chain: Vector[Trace] = Vector.empty
                 )

  case class NotifyMessage(
    body: String,
    additional: Map[String, String] = Map.empty
  )

  sealed trait Result {
    def err: Int
  }
  case class SuccessResult(err: Int, result: String) extends Result
  case class FailureResult(err: Int, message: String) extends Result

}
