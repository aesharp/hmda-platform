package hmda.api.processing

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props }
import hmda.persistence.CommonMessages.Event
import hmda.persistence.processing.HmdaRawFile.{ UploadCompleted, UploadStarted }
import hmda.persistence.processing.HmdaRawFileQuery
import hmda.persistence.processing.HmdaRawFileQuery.ReadHmdaRawData

object LocalHmdaEventProcessor {
  def props(): Props = Props(new LocalHmdaEventProcessor)

  def createLocalHmdaEventProcessor(system: ActorSystem): ActorRef = {
    system.actorOf(LocalHmdaEventProcessor.props())
  }

}

class LocalHmdaEventProcessor extends Actor with ActorLogging {

  val hmdaRawFileQuery = context.actorOf(HmdaRawFileQuery.props())

  override def preStart(): Unit = {
    context.system.eventStream.subscribe(self, classOf[Event])
  }

  override def receive: Receive = {

    case e: Event => e match {
      case UploadStarted(submissionId) =>
        log.debug(s"Upload started for submission $submissionId")

      case UploadCompleted(size, submissionId) =>
        fireUploadCompletedEvents(size, submissionId)

      case _ => //ignore other events

    }
  }

  private def fireUploadCompletedEvents(size: Int, submissionId: String): Unit = {
    log.debug(s"$size lines uploaded for submission $submissionId")
    hmdaRawFileQuery ! ReadHmdaRawData(submissionId)
  }
}
