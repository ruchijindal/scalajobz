package utils
import akka.actor.ActorSystem
import akka.actor.Props

case class JobAlertMail()
case class InitiateJobAlertMail()

object DailyJobAlert {

  /**
   * Send Mail To Job Seekers on the basis of their search criteria Via  Akka Actor
   */
  def sendMailIForJobAlert: Unit = {
    val system = ActorSystem("jobInitiateActors")
    val jobActor = system.actorOf(Props[JobAlertActorInitiator])
    jobActor ! InitiateJobAlertMail()
  }

}



