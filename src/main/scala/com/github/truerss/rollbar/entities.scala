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

  case class Body(message: NotifyMessage)

  case class NotifyMessage(
    body: String,
    additional: Map[String, String] = Map.empty
  )

  case class Result(err: Int, result: String)

}
