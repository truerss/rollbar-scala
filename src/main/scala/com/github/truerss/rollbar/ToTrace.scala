package com.github.truerss.rollbar

import entities.{Trace, Frame, RollBarException}

trait ToTrace {
  def convert(thr: Throwable): Trace
}

object DefaultImplicits {
  implicit val ToTraceDefault: ToTrace = new ToTrace {
    override def convert(thr: Throwable): Trace = {
      val fs = thr.getStackTrace.map { x =>
        Frame(
          fileName = x.getFileName,
          lineNumber = Option(x.getLineNumber),
          colNumber = None,
          method = Option(x.getMethodName),
          code = None,
          className = Option(x.getClassName),
          context = None,
          argSpec = Vector.empty,
          varargSpec = None,
          keyWordSpec = None
        )
      }.toVector

      Trace(frames = fs, exception = RollBarException(thr))
    }
  }
}