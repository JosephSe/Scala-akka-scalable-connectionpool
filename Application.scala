import akka.actor.Actor
import akka.actor.Actor.Receive

case class OpenConnection()
case class CloseConnection()

class Connection(id: String) extends Actor {
  override def receive: Receive = {
    case _ => println(s"Message recieved : $id")
  }

}

