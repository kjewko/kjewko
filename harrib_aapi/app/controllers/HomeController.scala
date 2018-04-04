package controllers

import java.sql.DriverManager
import java.sql.Connection
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
        else if (content == "xXx") {
          Status(400)(select(content))
        }
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

  def select(content: String) : JsObject = {
    val driver = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://localhost/mysql"
    val username = "root"
    val password = "toor"

    // there's probably a better way to do this
    var connection:Connection = null

    try {
      // make the connection
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)

      // create the statement, and run the select query
      var hosts : List[String] = null
      var users : List[String] = null
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery("SELECT host, user FROM user")
      while ( resultSet.next() ) {
        val host = resultSet.getString("host")
        val user = resultSet.getString("user")
        hosts = host :: hosts
        users = user :: users
      }
      JsObject(
        Seq(( "hosts" -> JsArray(hosts)),
            ( "users" -> JsArray(users))
        )
      )
    } catch {
      case e => JsObject(
        Seq(( "printStackTrace" -> JsString(e.toString)))
        )
    }
    connection.close()
  }
}