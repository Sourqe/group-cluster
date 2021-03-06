object ClusterManager {

  sealed trait ClusterMessage
  case object GetMembers extends ClusterMessage

  def props(nodeId: String) = Props(new ClusterManager(nodeId))
}

class ClusterManager(nodeId: String) extends Actor with ActorLogging {

  val cluster: Cluster = Cluster(context.system)
  val listener: ActorRef = context.actorOf(ListenerCluster.props(nodeId, cluster), "clusterListener")

  override def receive: Receive = {
    case GetMembers => {
      sender() ! cluster.state.members.filter(_.status == MemberStatus.up)
        .map(_.address.toString)
        .toList
    }
  }
}