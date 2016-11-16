package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import org.birdfeed.chirp.database.Query
import org.birdfeed.chirp.actions.ActionWithValidApiKey
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends Controller with Query {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = ActionWithValidApiKey(dbConfigProvider) {
    Action {
      Ok(views.html.index("Your new application is ready."))
    }
  }


}
