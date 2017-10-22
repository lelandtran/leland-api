package controllers

import javax.inject.{Inject, Singleton}

import org.slf4j.LoggerFactory
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AuthController @Inject() (cc: ControllerComponents, configuration: play.api.Configuration, ws: WSClient) extends AbstractController(cc){

  val CLIENT_ID_GITHUB = configuration.underlying.getString("auth.github.client_id")
  val CLIENT_ID_LINKEDIN = configuration.underlying.getString("auth.linkedin.client_id")
  val CLIENT_SECRET_GITHUB = configuration.underlying.getString("auth.github.client_secret")
  val CLIENT_SECRET_LINKEDIN = configuration.underlying.getString("auth.linkedin.client_secret")
  val MIME_TYPE_GITHUB = configuration.underlying.getString("api.mime.github")
  val URL_LINKEDIN_AUTH = "https://www.linkedin.com/oauth/v2/authorization"
  val URL_LINKEDIN_ACCESS = "https://www.linkedin.com/oauth/v2/accessToken"
  val URL_LINKEDIN_REDIRECT = "http://localhost:9000/auth/linkedin/callback"
  val URL_GITHUB_ACCESS = "https://github.com/login/oauth/access_token"
  val URL_GITHUB_AUTH = "https://github.com/login/oauth/authorize"

  def authLinkedIn = Action {
    val headers = Map(
      "response_type"->Seq("code"),
      "client_id"->Seq(CLIENT_ID_LINKEDIN),
      "redirect_uri"->Seq(URL_LINKEDIN_REDIRECT),
      "state"->Seq("STATE")
    )
    Redirect(URL_LINKEDIN_AUTH, headers, FOUND)
  }

  def authLinkedInCb(code: String, state: String) = Action.async { request =>
    ws.url(URL_LINKEDIN_ACCESS)
      .addHttpHeaders("Content-Type" -> "application/x-www-form-urlencoded")
      .post(Map(
        "client_id"->CLIENT_ID_LINKEDIN,
        "grant_type"->"authorization_code",
        "code"->code,
        "redirect_uri"->URL_LINKEDIN_REDIRECT,
        "client_secret"->CLIENT_SECRET_LINKEDIN
      ))
      .map { response =>
        Ok("Received response: " +
          formatResponse(response)
        )
    }

  }

  def authGithub = Action {
    Redirect(URL_GITHUB_AUTH, Map("client_id"->Seq(CLIENT_ID_GITHUB)), FOUND)
  }

  def authGithubCb(code: String) = Action.async { request =>
    ws.url(URL_GITHUB_ACCESS)
        .post(Json.obj(
          "code" -> code,
          "client_id" -> CLIENT_ID_GITHUB,
          "client_secret" -> CLIENT_SECRET_GITHUB
        ))
        .map { response =>
          Ok("Client Id: "+CLIENT_ID_GITHUB+".\n" +
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
