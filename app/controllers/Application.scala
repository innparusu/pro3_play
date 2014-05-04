package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import org.pac4j.http.client._
import org.pac4j.core.profile._
import org.pac4j.oauth.profile.twitter._
import org.pac4j.play._
import org.pac4j.play.scala._
import twitter4j._
import twitter4j.conf._
import twitter4j.auth._
import play.api.libs.json.Json

object Application extends ScalaController {

  def index = Action { request =>
    val newSession = getOrCreateSessionId(request)
    val urlTwitter = getRedirectAction(request, newSession, "TwitterClient", "/result").getLocation()
    Ok(views.html.index(urlTwitter)).withSession(newSession)
  } 

  def result = Action { request =>
    val profile:TwitterProfile = getUserProfile(request).asInstanceOf[TwitterProfile]
    val twitterApiKey = Play.application.configuration.getString("twitterApiKey").get
    val twitterSecret = Play.application.configuration.getString("twitterSecret").get
    var factory    = new TwitterFactory(new ConfigurationBuilder().setOAuthConsumerKey(twitterApiKey).setOAuthConsumerSecret(twitterSecret).build())
    var twitter    = factory.getInstance(new AccessToken(profile.getAccessToken(), profile.getAccessSecret()));
    Ok(views.html.result(twitter.getMentionsTimeline()))
  }
}
