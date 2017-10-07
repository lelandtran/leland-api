package controllers

import javax.inject._

import play.api.libs.json.{JsObject, JsString, JsTrue, JsValue}
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class TestController @Inject()(cc: ControllerComponents, ws:WSClient) extends AbstractController(cc) {


  def index = Action { request => {
    Ok("Got request [" + request + "]")
  }
  }

  def asyncProxy = Action.async { request => {
    ws.url("http://localhost:9000/test/dummy").get().map { response =>
      Ok(response.body)
    }}
  }

  def dummy = Action {
    val json: JsValue = JsObject(Map("hello"->JsString("world")))
    Ok(json)
  }
}

