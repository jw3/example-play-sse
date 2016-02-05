package example.sse

import akka.actor.ActorSystem

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.scaladsl.Source
import akka.stream.{ ActorMaterializer, Materializer }
import de.heikoseeberger.akkasse.{ EventStreamMarshalling, ServerSentEvent }
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

/**
 * taken from example on https://github.com/hseeberger/akka-sse
 */
object Boot extends App {
    implicit val system = ActorSystem()
    implicit val mat = ActorMaterializer()
    import system.dispatcher
    Http().bindAndHandle(route(system), "127.0.0.1", 9001)

    def route(system: ActorSystem)(implicit ec: ExecutionContext, mat: Materializer) = {
        import Directives._
        import EventStreamMarshalling._
        get {
            complete {
                Source.tick(2 seconds, 2 seconds, ())
                .map(_ => LocalTime.now())
                .map(dateTimeToServerSentEvent)
                .keepAlive(1 second, () => ServerSentEvent.heartbeat)
            }
        }
    }

    def dateTimeToServerSentEvent(time: LocalTime): ServerSentEvent = ServerSentEvent(
        DateTimeFormatter.ISO_LOCAL_TIME.format(time)
    )
}
