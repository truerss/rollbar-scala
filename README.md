# rollbar-scala

Api access for [rollbar](https://rollbar.com/)

# How to

```scala
import com.github.truerss.rollbar._

val token = "my-access-token"

val builder = NotifyBuilder(token, "environment")
Sender.send(builder) // Result(0, "some-uuid")

// or use own sender
import spray.json._
import com.github.truerss.rollbar.json._
val builder = NotifyBuilder(token, "environment")
MySender.send(builder.info("some-message").toJson)
```


# License: MIT


