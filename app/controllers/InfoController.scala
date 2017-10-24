package controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class InfoController @Inject() (cc:ControllerComponents, ws:WSClient, authController: AuthController) extends AbstractController(cc){

  def linkedin = Action.async {
    System.out.println("accessTokenLinkedIn: " + authController.accessTokenLinkedIn)

    ws.url("https://api.linkedin.com/v1/people/~?format=json")
      .addHttpHeaders(
        "Connection" -> "Keep-Alive",
        "Authorization" -> ("Bearer " + authController.accessTokenLinkedIn)
      )
      .get()
      .map{ response =>
        Ok(formatResponse(response))
    }
  }

  val formatResponse: WSResponse => String = (response => {
    val formatHeaders: (StringBuilder, (String, Seq[String]))=> StringBuilder = ((sb, header) =>
      sb.append("\t(").append(header._1).append(")->(").append(header._2).append(")\n"))

    "Got response " + response.status + ".\n" +
      "Headers: \n" +
      response.headers.foldLeft(new StringBuilder(""))(formatHeaders).toString() + ".\n"+
      "Body: " + response.body + ".\n"
  })
}
