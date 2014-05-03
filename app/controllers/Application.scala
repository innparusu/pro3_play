package controllers

import play.api._
import play.api.mvc._
import org.pac4j.http.client._
import org.pac4j.core.profile._
import org.pac4j.play._
import org.pac4j.play.scala._
import play.api.libs.json.Json

object Application extends ScalaController {

  def index = Action { request =>
    val newSession = getOrCreateSessionId(request)
    val urlTwitter = getRedirectAction(request, newSession, "TwitterClient", "/result").getLocation()
    Ok(views.html.index(urlTwitter)).withSession(newSession)
  }

  def result = Action { request =>
    val profile    = getUserProfile(request)
    Ok(views.html.result(profile))
  }
}
