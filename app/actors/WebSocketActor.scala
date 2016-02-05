package actors

import actors.WebSocketActor.Completed
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import de.heikoseeberger.akkasse.EventStreamUnmarshalling._
import de.heikoseeberger.akkasse.ServerSentEvent

import scala.util.{Failure, Success}


object WebSocketActor {
    def props(out: ActorRef, mat: ActorMaterializer) = Props(new WebSocketActor(out, mat))

    case class Completed()
}

class WebSocketActor(out: ActorRef, mat: ActorMaterializer) extends Actor with ActorLogging {
    implicit val m = mat
    implicit val s = context.system
    implicit val d = context.system.dispatcher

    def receive = {
        case msg: String =>
            out ! s"echoing: $msg"
    }

    override def preStart(): Unit = {
        log.warning("starting stream")
        out ! "starting stream"
        val f = sseSource().runForeach(_.map { sse => println(sse); sse.data }.runWith(Sink.actorRef(out, Completed())))
        f.onComplete {
            case Success(_) => out ! "complete"
            case Failure(ex) => out ! ex.getMessage
        }
    }

    def sseSource() = {
        Source.single(Get())
        .via(Http().outgoingConnection("127.0.0.1", 9001))
        .mapAsync(1)(Unmarshal(_).to[Source[ServerSentEvent, Any]])
    }
}
