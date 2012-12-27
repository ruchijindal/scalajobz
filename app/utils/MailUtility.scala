package utils

import javax.mail.internet.MimeMessage
import java.util.Properties
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.Message
import javax.mail.Transport
import org.bson.types.ObjectId
import play.api.Play
import models.JobEntity
import models.Common
import models.UserEntity

object MailUtility {

  val mailServer = "scalajobz@gmail.com"
  val supportMailString = "support@scalajobz.com"
  val serverProtocol = "smtp.gmail.com"
  val email_password = "email_password"
  val protocolName = "smtp"
  val break = "<br/>"

  /**
   * Send mail to user for job alerts
   * @param jobSeeker is the mail receiver
   * @param jobs are the jobs matching the seekers skills
   */
  def sendEmail(jobSeeker: UserEntity, jobs: List[JobEntity]): Unit = {
    val props = new Properties
    props.setProperty("mail.transport.protocol", protocolName)
    props.setProperty("mail.smtp.starttls.enable", "true")
    props.setProperty("mail.host", serverProtocol)
    props.setProperty("mail.user", mailServer)
    props.setProperty("mail.password", ConversionUtility.decodeMe(Play.current.configuration.getString(email_password).get))
    val session = Session.getDefaultInstance(props, null);
    val msg = new MimeMessage(session)
    val recepientAddress = new InternetAddress(jobSeeker.emailId)
    msg.setFrom(new InternetAddress(supportMailString, supportMailString))
    msg.addRecipient(Message.RecipientType.TO, recepientAddress);
    msg.setSubject("Job Alert From ScalaJobz.com");
    msg.setContent(Common.setContentForJobAlert(jobs, jobSeeker), "text/html")
    val transport = session.getTransport(protocolName);
    transport.connect(serverProtocol, mailServer, ConversionUtility.decodeMe(Play.current.configuration.getString(email_password).get))
    transport.sendMessage(msg, msg.getAllRecipients)
  }

  /**
   * Forgot password functionality
   * @param emailId is the mailId f password
   * @param password is the password of that user
   *
   */
  def sendPassword(emailId: String, password: String) {
    val props = new Properties
    props.setProperty("mail.transport.protocol", protocolName)
    props.setProperty("mail.smtp.starttls.enable", "true")
    props.setProperty("mail.host", serverProtocol)
    props.setProperty("mail.user", mailServer)
    props.setProperty("mail.password", ConversionUtility.decodeMe(Play.current.configuration.getString(email_password).get))

    val session = Session.getDefaultInstance(props, null)
    val msg = new MimeMessage(session)
    val recepientAddress = new InternetAddress(emailId)
    msg.setFrom(new InternetAddress(supportMailString, supportMailString))
    msg.addRecipient(Message.RecipientType.TO, recepientAddress);
    msg.setSubject("Password Recovery On ScalaJobz")

    msg.setContent(

      "Hi <b>ScalaJobz</b> User." + break + break +
        "Here is your account details " + break + break +
        "Email-Id: " + emailId + break +
        "Password: " + password + break +
        break + break + break +
        "Cheers," + break +
        "ScalaJobz" + break, "text/html")

    val transport = session.getTransport(protocolName)
    transport.connect(serverProtocol, mailServer, ConversionUtility.decodeMe(Play.current.configuration.getString(email_password).get))
    transport.sendMessage(msg, msg.getAllRecipients)
  }

  /**
   * Sent mail to Scalajobz when anyone make a suggestion on contact us page
   * @param name is mail sender name
   * @param emailAddress is sender email id
   * @param subject is mail subject
   * @param message is sender message
   */
  def sendEmailToScalaJobzFromContactUs(name: String, senderEmailAddress: String, subject: String, message: String): Unit = {
    val props = new Properties
    props.setProperty("mail.transport.protocol", protocolName)
    props.setProperty("mail.smtp.starttls.enable", "true")
    props.setProperty("mail.host", serverProtocol)
    props.setProperty("mail.user", mailServer)
    props.setProperty("mail.password", ConversionUtility.decodeMe(Play.current.configuration.getString(email_password).get))
    val session = Session.getDefaultInstance(props, null);
    val msg = new MimeMessage(session)
    val recepientAddress = new InternetAddress(mailServer)
    msg.setFrom(new InternetAddress(supportMailString, supportMailString))
    msg.addRecipient(Message.RecipientType.TO, recepientAddress);
    msg.setSubject("Contact Us ScalaJobz : " + subject);
    msg.setContent(Common.setContentForContactUsMail(name, senderEmailAddress, subject, message), "text/html")
    val transport = session.getTransport(protocolName);
    transport.connect(serverProtocol, mailServer, ConversionUtility.decodeMe(Play.current.configuration.getString(email_password).get))
    transport.sendMessage(msg, msg.getAllRecipients)
  }

  /**
   * Acknowledgement Mail
   * @param jobSeeker is the mail receiver
   * @param jobs are the jobs matching the seekers skills
   */
  def acknowledgementMail(name: String, senderEmailAddress: String): Unit = {
    val props = new Properties
    props.setProperty("mail.transport.protocol", protocolName)
    props.setProperty("mail.smtp.starttls.enable", "true")
    props.setProperty("mail.host", serverProtocol)
    props.setProperty("mail.user", mailServer)
    props.setProperty("mail.password", ConversionUtility.decodeMe(Play.current.configuration.getString(email_password).get))
    val session = Session.getDefaultInstance(props, null);
    val msg = new MimeMessage(session)
    val recepientAddress = new InternetAddress(senderEmailAddress)
    msg.setFrom(new InternetAddress(supportMailString, supportMailString))
    msg.addRecipient(Message.RecipientType.TO, recepientAddress);
    msg.setSubject("Thanks For Mailing Us ScalaJobz");
    msg.setContent(Common.setContentForAcknowledgementMail(name), "text/html")
    val transport = session.getTransport(protocolName);
    transport.connect(serverProtocol, mailServer, ConversionUtility.decodeMe(Play.current.configuration.getString(email_password).get))
    transport.sendMessage(msg, msg.getAllRecipients)
  }

}
