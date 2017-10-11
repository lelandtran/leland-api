package controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class AuthController @Inject() (cc: ControllerComponents, ws: WSClient) extends AbstractController(cc){

  def authGithubCb(code: String) = Action { request =>
    ws.url("https://github.com/login/oauth/access_token").post(Json.obj(
      "code" -> code,
      "client_id" -> "CLIENT_ID",
      "client_secret" -> "CLIENT_SECRET")).map { response =>

      Ok("Got request [" + request + "] with code " + code)
    }
    Ok("Sent access token request, redirecting...")
  }
}
