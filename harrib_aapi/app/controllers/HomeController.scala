package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(content: String) = Action { implicit request: Request[AnyContent] =>
    Status(404)(api404())
  }

  def apiContent(content: String) = Action { implicit request: Request[AnyContent] =>

      content match {
        case "domains.json" =>
          Status(200)(JsObject(
            Seq(( "code"      ->  JsNumber(200)),
                ( "message"   ->  JsString("success")),
                ( "datas"       ->  JsArray(IndexedSeq(
                  JsObject(Seq(
                      "id"  ->  JsNumber(1),
                      "slug"  ->  JsString("mailer"),
                      "name"  ->  JsString("mailer"),
                      "description"  ->  JsString("Liste des mails automatisé")
                  )),
                  JsObject(Seq(
                      "id"  ->  JsNumber(2),
                      "slug"  ->  JsString("documents"),
                      "name"  ->  JsString("documents"),
                      "description"  ->  JsString("un petit teste de documents a traduire.")
                  ))
              )))
            )
          ))
        case _ =>
        if (content.contains("domains."))
          Status(400)(api400)
        else
          Status(404)(api404())
      }    
  }

  def api404() = JsObject(
      Seq(( "code"      ->  JsNumber(404)),
          ( "message"   ->  JsString("not found"))
      )
    )
  

  def api400() = JsObject(
      Seq(( "code"      ->  JsNumber(400)),
          ( "message"   ->  JsString("error")),
          ( "datas"     ->  JsArray())
      )
    )
   
}