import akka.actor.Actor
import akka.actor.Actor.Receive

case class OpenConnection()
case class CloseConnection()

class Connection(id: String) extends Actor {
  override def receive: Receive = {
    case _ => println(s"Message recieved : $id")
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    println("Opening Connection")
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    super.postStop()
    println("Closing Connection")
  }

}

