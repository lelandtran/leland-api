package controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AuthController @Inject() (cc: ControllerComponents, ws: WSClient) extends AbstractController(cc){

  def authGithubCb(code: String) = Action { request =>
    ws.url("https://github.com/login/oauth/access_token").post(Json.obj(
      "code" -> code,
      "client_id" -> "CLIENT_ID",
      "client_secret" -> "CLIENT_SECRET")).map { response =>
      System.out.println(response)
      Ok("Sent request with code " + code)
      // this secondary async response does not work;
      // should check if one response to one request is not just a limitation in java, but the protocol
    }
    Ok("Sent access token request with code " + code + ", redirecting...")
  }
}
