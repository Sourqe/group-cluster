object ListenerCluster {
  def props(nodeId: String, cluster: Cluster) = Props(new ListenerCluster(nodeId, cluster))
}

class ListenerCluster(nodeId: String, cluster: Cluster) extends Actor with ActorLogging {

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case MemberUp(member) =>
      log.info("Node {} - Member is awake: {}", nodeId, member.address)
    case UnreachableMember(member) =>
      log.info(s"Node {} - Member unreachable: {}", nodeId, member)
    case MemberRemoved(member, previousStatus) =>
      log.info(s"Node {} - Member removed: {} after {}",
        nodeId, member.address, previousStatus)
    case _: MemberEvent =>
  }
}