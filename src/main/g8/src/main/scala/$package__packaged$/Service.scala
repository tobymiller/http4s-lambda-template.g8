package $package$

import cats.effect.Sync
import io.circe.Json
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import io.github.howardjohn.lambda.http4s.Http4sLambdaHandler
import cats.effect.IO
import java.io.OutputStream
import java.io.InputStream
import org.http4s.HttpService
import org.http4s.server.blaze.BlazeBuilder
import scala.concurrent.ExecutionContext
import fs2.StreamApp

object Service extends StreamApp[IO] with Http4sDsl[IO] {
  val service: HttpService[IO] = HttpService[IO] {
    case GET -> Root \/ "hello" \/ name => Ok(s"Hello, $name")
  }

  // used as entry point for serverless
  class Routes extends Http4sLambdaHandler(service)
  
  import scala.concurrent.ExecutionContext.Implicits.global
  
  // main method, runs via StreamApp for local testing
  def stream(args: List[String], requestShutdown: IO[Unit]) =
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(Service.service, "\/")
      .serve
}
