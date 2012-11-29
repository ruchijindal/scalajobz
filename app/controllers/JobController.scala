package controllers
import play.api.mvc.Controller
import play.api.data.Forms.nonEmptyText
import play.api.data.Form
import play.api.mvc.Controller
import play.api.mvc
import play.api.data
import play.api.data.Forms
import play.mvc.Http.Request
import org.bson.types.ObjectId
import models.PostAJobForm
import models.JobEntity
import models.Job
import java.util.Date
import models.Alert
import models.Common
import play.api.mvc.Action
import play.api.mvc.Results

object JobController extends Controller {

  val postAJobForm = Form(
    Forms.mapping(
      "Position" -> nonEmptyText,
      "Company" -> nonEmptyText,
      "Location" -> nonEmptyText,
      "JobType" -> nonEmptyText,
      "Email_Addrss_To_Apply_To" -> nonEmptyText,
      "Skills" -> nonEmptyText,
      "Description" -> nonEmptyText)(PostAJobForm.apply)(PostAJobForm.unapply))

  /**
   * Load  Job  Page on scalajobz.com
   */

  def postAJob: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    if(request.session.get("userId") == None){
      Ok(views.html.login(new Alert("", ""), Application.logInForm, request.session.get("userId").getOrElse(""), "jobPost"))
    } else{
      Ok(views.html.postajob(postAJobForm, request.session.get("userId").getOrElse("")))
  }}

  /**
   * Post A Job on scalajobz.com
   */
  def newJob: Action[play.api.mvc.AnyContent] = Action { implicit request =>
    postAJobForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(new Alert("error", "There Was Some Errors During Job Posting"),
        request.session.get("userId").getOrElse(""), Job.findAllJobs, false)),
      postAJobForm => {
        if (postAJobForm.position == "" || postAJobForm.company == "" || postAJobForm.location == ""
          || postAJobForm.jobType == "" || postAJobForm.jobType.equals("-- Select Job Type --") ||
          postAJobForm.emailAddress == "") { Ok("Please Fill The Mendatory Fields") }
        else {
          if (request.session.get("userId") == None) {
            Ok(views.html.login(new Alert("", ""),
              Application.logInForm, request.session.get("userId").getOrElse(""), "jobPost"))
          } else {
            val job = JobEntity(new ObjectId, new ObjectId(request.session.get("userId").get),
              postAJobForm.position, postAJobForm.company, postAJobForm.location, postAJobForm.jobType,
              postAJobForm.emailAddress, postAJobForm.skillsRequired.split(",").toList, postAJobForm.description, new Date)
            Job.addJob(job)
            Common.setAlert(new Alert("success", "Job Posted Successfully"))
            Results.Redirect("/findJobPostByUserId")
          }
        }
      })
  }

  /**
   * Load  Job  Page on scalajobz.com
   */

  def findAJob(searchString: String, editFlag: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val searchJobList = Job.searchTheJob(searchString)
    if(editFlag.equals("true")){
      Ok(views.html.ajax_result(searchJobList, true))
    } else{
      Ok(views.html.ajax_result(searchJobList, false))
  }}

  /**
   * Find Job Detail By JobId
   */
  def findJobDetail(jobId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val job: Option[JobEntity] = Job.findJobDetail(new ObjectId(jobId))
    Ok(views.html.jobDetail(job.get, request.session.get("userId").getOrElse("")))
  }

  /**
   * Find Job Detail By JobId For Edit
   */
  def findJobPostForEdit(jobId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val job = Job.findJobDetail(new ObjectId(jobId)).get
    Ok(views.html.editJob(job, postAJobForm, request.session.get("userId").getOrElse("")))
  }

  /**
   * Edit Job
   */
  def editJob(jobId: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    val existJob = Job.findJobDetail(new ObjectId(jobId))
    existJob match {
      case None => Results.Redirect(routes.UserController.findJobPostByUserId)
      case Some(job: JobEntity) =>
        postAJobForm.bindFromRequest.fold(
          errors => BadRequest(views.html.editJob(job, postAJobForm, request.session.get("userId").getOrElse(""))),
          postAJobForm => {
            val editJob = JobEntity(job.id, job.userId, postAJobForm.position, postAJobForm.company
                ,postAJobForm.location, postAJobForm.jobType, postAJobForm.emailAddress, postAJobForm.skillsRequired.split(",").toList,
                postAJobForm.description, new Date)
            Job.updateJob(editJob)
            Common.setAlert(new Alert("success", "Job Posted Successfully"))
            Results.Redirect("/findJobPostByUserId")
          })
    }
  }

  /**
   * Delete Job by JobId
   */

  def deleteJob(jobId: String, editFlag: String): Action[play.api.mvc.AnyContent] = Action { implicit request =>
    Job.deleteJobByJobId(new ObjectId(jobId))
    val jobPostByUserList = Job.findJobsPostByUserId(new ObjectId(request.session.get("userId").get))
    if(editFlag.equals("true")){
      Ok(views.html.ajax_result(jobPostByUserList, true))
    } else{
      Ok(views.html.ajax_result(jobPostByUserList, false))
  }}

}