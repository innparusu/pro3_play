package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action { request =>
    val newSession = getOrCreateSessionId()
    val urlTwitter = getRedirectAction(request, newSession, "twitterClient", "/").getLocation()
    val profile    = getUserProfile(request)
    Ok(views.html.index(profile, urlTwitter)).withSession(newSession)
  }
}
