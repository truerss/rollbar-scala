# rollbar-scala

Easy Api access for [rollbar](https://rollbar.com/)

Current version: 0.0.4

# How to

add dependency

`"com.github.truerss" %% "rollbar-scala" % "0.0.4"`

and then

```scala
import com.github.truerss.rollbar._

val token = "your-access-token" // <- post_server_item

val builder = NotifyBuilder(token, "environment")
Sender.send(builder.info("some message")) // Result(0, "some-uuid")

// or use own sender
import spray.json._
import com.github.truerss.rollbar.json._
val builder = NotifyBuilder(token, "environment")
MySender.send(builder.info("some message").toJson)

// send exception

import DefaultImplicits._ // or implement own

val builder = NotifyBuilder(token, "environment")

try {
  // ... throw new RuntimeException("boom!")
} catch {
  case ex: Throwable =>
    Sender.send(builder.trace(ex))
}

```


# License: MIT


