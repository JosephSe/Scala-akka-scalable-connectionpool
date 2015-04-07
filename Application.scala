import akka.actor._
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

case class OpenConnection()

case class CloseConnection()

case class Process(message: String)


class Connection(id: String) extends Actor {
  override def receive: Receive = {
    case mess: Process => println(s"Message recieved : $id - ${mess.message}")
      Thread.sleep(1000)
  }

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    println(s"Opening Connection $id")
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    super.postStop()
    println(s"Closing Connection : $id")
  }

}

class ConnectionPool(max: Integer = 10) extends Actor {

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

object Application extends App {
  var system = ActorSystem.create("connectionPool")
  var connectionPool = system.actorOf(Props(new ConnectionPool))
  connectionPool ! Process("test1")
  connectionPool ! OpenConnection
  connectionPool ! OpenConnection
  connectionPool ! OpenConnection
  1 until 10 foreach { i =>
    connectionPool ! Process(s"test 2-$i")
  }
  connectionPool ! Process("test3")
  connectionPool ! CloseConnection
  connectionPool ! CloseConnection
  connectionPool ! CloseConnection
  1 until 10 foreach { i =>
    connectionPool ! Process(s"test 4-$i")
  }
  1 until 10 foreach { i =>
    connectionPool ! Process(s"test 2-$i")
  }
}
