package models
import net.liftweb.json.JsonDSL
import net.liftweb.json.MappingException
import net.liftweb.json.TypeInfo
import net.liftweb.json.Formats
import net.liftweb.json.JsonAST.JString
import net.liftweb.json.Serialization
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.NoTypeHints
import net.liftweb.json.CustomSerializer
import net.liftweb.json.JsonAST.JObject
import org.bson.types.ObjectId
import net.liftweb.json.JsonAST.JField
import net.liftweb.json.Serializer
import net.liftweb.json.JsonAST.JInt
import net.liftweb.json.TypeInfo
import play.api.Play

/**
 * class to show alert
 * @param alertType the Alert Type
 * @param message the message on Alert
 */
case class Alert(alertType: String,
  message: String)

object Common {

  val break = "<br/>"
  var alert: Alert = new Alert("", "")
  def setAlert(alert: Alert): Unit = this.alert = alert

  /**
   * Set Content For Sending Mail For Daily Job Alert
   * @param jobs is the list of jobs that matches the jobseekere's skills
   * @param jobSeeker is the jobseeker with same the skills required for job 
   */

   def setContentForJobAlert(jobs: List[JobEntity], jobSeeker: UserEntity): String = {

    val removeJobAlertLink = "http://" + getContextUrl + "/unSubscribeJobSeeker/" + jobSeeker.id
    var message = "<b>Job Alert from scalajobz.com</b>" +
      break + break + "<b>Your Job Details</b>" + break + break
    for (job <- jobs) {
      val redirectToJobLink = "http://" + getContextUrl + "/jobDetail/" + job.id
      message += "<b><u><a href= " + redirectToJobLink + ">" + job.position + "</a></u></b>" + break
      message += job.company + " - " + job.location + break
      if (job.description.length > 150) {
        message += job.description.substring(0, 150) + " ..." + break
      } else {
        message += job.description + break
      }

    }
    message += "<br/>Click  <u>" + removeJobAlertLink + "</u> to unsubscribe from ScalaJobz"
    message
  }


  /**
   * To get The root context from application.config
   */
  def getContextUrl: String = {
    Play.current.configuration.getString("contextUrl").get
  }

}

/**
 * Override Object To get Object Id In json Response
 */
class ObjectIdSerializer extends Serializer[ObjectId] {
  private val Class = classOf[ObjectId]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), ObjectId] = {
    case (TypeInfo(Class, _), json) => json match {
      case JInt(s) => new ObjectId
      case JString(s) => new ObjectId(s)
      case x: Any => throw new MappingException("Can't convert " + x + " to ObjectId")
    }
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: ObjectId => JObject(JField("id", JString(x.toString)) :: Nil)

  }
}
