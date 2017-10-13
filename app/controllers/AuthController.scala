package controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AuthController @Inject() (cc: ControllerComponents, configuration: play.api.Configuration, ws: WSClient) extends AbstractController(cc){

  val CLIENT_ID = configuration.underlying.getString("auth.github.client_id")
  val CLIENT_SECRET = configuration.underlying.getString("auth.github.client_secret")
  val URL_GITHUB_ACCESS = "https://github.com/login/oauth/access_token"
  val URL_GITHUB_AUTH = "https://github.com/login/oauth/authorize"

  def authGithub = Action {
    Redirect(URL_GITHUB_AUTH, Map("client_id"->Seq(CLIENT_ID)), FOUND)
  }

  def authGithubCb(code: String) = Action.async { request =>
    ws.url(URL_GITHUB_ACCESS).post(Json.obj(
      "code" -> code,
      "client_id" -> CLIENT_ID,
      "client_secret" -> CLIENT_SECRET))
      .map { response =>
        val body = response.body
        val headers = response.headers
        val status = response.status
        Ok("Client Id: "+CLIENT_ID+".\n" +
          "Sent request with code " + code + ".\n" +
          "Got response " + status + ".\n" +
          "Headers: \n" + headers.foldLeft(new StringBuilder(""))((sb, header) =>
            sb.append("\t(").append(header._1).append(")->(").append(header._2).append(")\n")
          ).toString() + ".\n"+
          "Body: " + body + ".\n")
    }
  }
}
