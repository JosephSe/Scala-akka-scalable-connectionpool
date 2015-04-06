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

  var router = {
    val initialPoolSize = 1
    val initialRoutees = Seq.fill(initialPoolSize) {
      val worker = context.actorOf(Props(new Connection(1.toString)))
      context watch worker
      ActorRefRoutee(worker)
    }
    Router(RoundRobinRoutingLogic(), initialRoutees.toIndexedSeq)
  }

  override def receive: Actor.Receive = {
    case OpenConnection => addConnection()
    case CloseConnection => removeConnection()
    case mess: AnyRef => router.route(mess, sender())
  }

  def addConnection() = {
    val size = router.routees.size
    if (size < max) {
      val connection = context.actorOf(Props(new Connection((size + 1).toString)))
      context watch connection
      router = router.addRoutee(connection)
    }
    printRoutes()
  }

  def removeConnection() = {
    println("")
    val headRef = router.routees.head.asInstanceOf[ActorRefRoutee].ref
    headRef ! PoisonPill
    context unwatch headRef
    router = router.removeRoutee(headRef)
    printRoutes
  }

  def printRoutes(): Unit = {
    val size = router.routees.size

    println(s"Total connections $size")
  }
}

