object FibProcessor {
  sealed trait FibProcessorMessage
  case class Compute(n: Int, replyTo: ActorRef) extends FibProcessorMessage

  def props(nodeId: String) = Props(new FibProcessor(nodeId))

  def fibonacci(x: Int): BigInt = {
    @tailrec def fibHelper(x: Int, prev: BigInt = 0, next: BigInt = 1): BigInt = x match {
      case 0 => prev
      case 1 => next
      case _ => fibHelper(x - 1, next, next + prev)
    }
    fibHelper(x)
  }
}

class FibProcessor(nodeId: String) extends Actor {
  import FibProcessor._

  override def receive: Receive = {
    case Compute(value, replyTo) => {
      replyTo ! ProcessorResponse(nodeId, fibonacci(value))
    }
  }
}