package controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AuthController @Inject() (cc: ControllerComponents, configuration: play.api.Configuration, ws: WSClient) extends AbstractController(cc){

  val clientId = configuration.underlying.getString("auth.github.client_id")
  val clientSecret = configuration.underlying.getString("auth.github.client_secret")

  def authGithubCb(code: String) = Action.async { request =>
    ws.url("https://github.com/login/oauth/access_token").post(Json.obj(
      "code" -> code,
      "client_id" -> clientId,
      "client_secret" -> clientSecret))
      .map { response =>
        val body = response.body
        val headers = response.headers
        val status = response.status
        Ok("Client Id: "+clientId+".\n" +
          "Sent request with code " + code + ".\n" +
          "Got response " + status + ".\n" +
          "Headers: \n" + headers.foldLeft(new StringBuilder(""))((sb, header) =>
            sb.append("\t(").append(header._1).append(")->(").append(header._2).append(")\n")
          ).toString() + ".\n"+
          "Body: " + body + ".\n")
    }
  }
}
