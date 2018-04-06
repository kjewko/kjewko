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

  def apiDomain(content: String) = Action { implicit request: Request[AnyContent] =>
	content match {
	  case "mailer.json" =>
	    Status(200)(step2(content))
	  case _ =>
            if (content.contains("mailer."))
              Status(400)(api400)
            else
              Status(404)(api404())
        }
  }

  def apiContent(content: String) = Action { implicit request: Request[AnyContent] =>

      content match {
        case "domains.json" =>
          Status(200)(/*JsObject(
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
          )*/step1(content))
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
    val url = "jdbc:mysql://localhost/etna_crowding"
    val username = "root"
    val password = "toor"

    // there's probably a better way to do this
    var connection:Connection = null

      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)

      // create the statement, and run the select query
      var hosts : List[String] = Nil
      var users : List[String] = Nil
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery("SELECT id, domain_id FROM translation")
      while ( resultSet.next() ) {
        val host = resultSet.getString("id")
        val user = resultSet.getString("domain_id")
        hosts = host :: hosts
        users = user :: users
	println(host)
	println(users)
      }
	JsObject(
        Seq(( "hosts" -> JsArray(hosts.map {
          case e => JsString(e)
        })),
            ( "users" -> JsArray(users.map {
          case e => JsString(e)
        }))
        )
      )
  }

  def step1(content: String) : JsObject = {
    val driver = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://localhost/etna_crowding"
    val username = "root"
    val password = "toor"

    // there's probably a better way to do this
    var connection:Connection = null
      // make the connection
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)

      // create the statement, and run the select query
      var hosts : List[String] = Nil
      var users : List[String] = Nil
      var setOut : Seq[JsValue] = Nil
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery("SELECT id, slug, name, description FROM domain")
      while ( resultSet.next() ) {
        val id = resultSet.getInt("id")
        val slug = resultSet.getString("slug")
        val name = resultSet.getString("name")
        val description = resultSet.getString("description")
        val putIn : JsValue = JsObject(Seq(
          "id"  ->  JsNumber(id),
          "slug"  ->  JsString(slug),
          "name"  ->  JsString(name),
          "description"  ->  JsString(description)
        ))
        setOut = setOut :+ putIn
      }
      JsObject(
        Seq(( "code"      ->  JsNumber(200)),
          ( "message"   ->  JsString("success")),
          ( "datas"       ->  JsArray(setOut))
        )
      )
  }

  def step2(content : String) : JsObject = {
    val driver = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://localhost/etna_crowding"
    val username = "root"
    val password = "toor"

    // there's probably a better way to do this
    var connection:Connection = null
    // make the connection
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)

    // create the statement, and run the select query
    var setOut : Seq[JsObject] = Nil
    val statement = connection.createStatement()
    var langs : List[String] = Nil
    val resultSet = statement.executeQuery("SELECT domain.id, domain.slug, domain.name, domain.description, domain.created_at, user.id AS user_id, user.username AS user_name FROM domain INNER JOIN user ON user.id = domain.id WHERE domain.slug = 'mailer';")
    var id = 0
    var slug = ""
    var name = ""
    var domain_description = ""
    var domain_created_at = ""
    var user_id = 0
    var user_username = ""
    while ( resultSet.next() ) {
      id = resultSet.getInt("domain.id")
      slug = resultSet.getString("domain.slug")
      name = resultSet.getString("domain.name")
      domain_description = resultSet.getString("domain.description")
      domain_created_at = resultSet.getString("domain.created_at")
      user_id = resultSet.getInt("user_id")
      user_username = resultSet.getString("user_name")
    }
    val resultLangs = statement.executeQuery("SELECT lang_id FROM domain_lang WHERE domain_lang.domain_id='" + id + "';")
    while ( resultLangs.next() ) {
        val lang = resultLangs.getString("lang_id")
        langs = lang :: langs
      }
      val putIn : JsObject = JsObject(Seq(
        "langs" -> JsArray(langs.map {
          case e => JsString(e)
        }),
        "id"  ->  JsNumber(id),
        "slug"  ->  JsString(slug),
        "name"  ->  JsString(name),
        "description"  ->  JsString(domain_description),
        "creator" -> JsObject(Seq(
          "id" -> JsNumber(user_id),
          "username" -> JsString(user_username)
        )),
        "created_at" -> JsString(domain_created_at)
      ))
      setOut = setOut :+ putIn
      JsObject(
      Seq(( "code"      ->  JsNumber(200)),
        ( "message"   ->  JsString("success")),
        ( "datas"       ->  setOut(0))
      )
    )
  }
}
