package controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AuthController @Inject() (cc: ControllerComponents, configuration: play.api.Configuration, ws: WSClient) extends AbstractController(cc){

  val CLIENT_ID = configuration.underlying.getString("auth.github.client_id")
  val CLIENT_SECRET = configuration.underlying.getString("auth.github.client_secret")
  val MIME_TYPE_GITHUB = configuration.underlying.getString("api.mime.github")
  val URL_LINKEDIN_AUTH = "https://www.linkedin.com/oauth/v2/authorization"
  val URL_GITHUB_ACCESS = "https://github.com/login/oauth/access_token"
  val URL_GITHUB_AUTH = "https://github.com/login/oauth/authorize"

  def authLinkedIn = Action {
    val headers = Map(
      "response_type"->Seq("code"),
      "client_id"->Seq("CLIENT_ID"),
      "redirect_uri"->Seq("http://localhost:9000/auth/linkedin/callback"),
      "state"->Seq("STATE")
    )
    Redirect(URL_LINKEDIN_AUTH, headers, FOUND)
  }

  def authLinkedInCb(code: String, state: String) = Action {
    System.out.println("Code: " + code + ", State: " + state)
    Ok("Received code: " + code + ", State: " + state)
  }

  def authGithub = Action {
    Redirect(URL_GITHUB_AUTH, Map("client_id"->Seq(CLIENT_ID)), FOUND)
  }

  def authGithubCb(code: String) = Action.async { request =>
    ws.url(URL_GITHUB_ACCESS)
        .post(Json.obj(
          "code" -> code,
          "client_id" -> CLIENT_ID,
          "client_secret" -> CLIENT_SECRET
        ))
        .map { response =>
          Ok("Client Id: "+CLIENT_ID+".\n" +
            "Sent request with code " + code + ".\n" +
            formatResponse(response)
            )
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
