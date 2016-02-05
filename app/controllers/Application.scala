package controllers

import javax.inject.{Inject, _}

import actors.WebSocketActor
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.Play.current
import play.api.mvc.{Action, Controller, _}


@Singleton
class Application @Inject()(implicit system: ActorSystem) extends Controller {
    implicit val mat = ActorMaterializer()

    val index = Action {
        Ok(views.html.index("1"))
    }

    def socket = WebSocket.acceptWithActor[String, String] { request => out =>
        WebSocketActor.props(out, mat)
    }
}
