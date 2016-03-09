# rollbar-scala

Easy Api access for [rollbar](https://rollbar.com/)

# How to

add dependency

`"com.github.truerss" %% "rollbar-scala" % "0.0.1"`

and then

```scala
import com.github.truerss.rollbar._

val token = "you-access-token" // <- post_server_item

val builder = NotifyBuilder(token, "environment")
Sender.send(builder.info("some message")) // Result(0, "some-uuid")

// or use own sender
import spray.json._
import com.github.truerss.rollbar.json._
val builder = NotifyBuilder(token, "environment")
MySender.send(builder.info("some message").toJson)
```


# License: MIT


