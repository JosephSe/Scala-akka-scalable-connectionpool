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

class ConnectionPool extends Actor {
  val max = 10
  var count = 0

  override def receive: Actor.Receive = {
    case OpenConnection => openNewConnection()
    case CloseConnection => closeConnection()
  }

  def openNewConnection() = {
    if(count < max) {
      count += 1

    }
  }

  def closeConnection() = {

  }
}

