package controllers

import scala.collection.JavaConversions._
import java.util.ArrayList
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
    val twitterApiKey      = Play.application.configuration.getString("twitterApiKey").get
    val twitterSecret      = Play.application.configuration.getString("twitterSecret").get
    var factory            = new TwitterFactory(new ConfigurationBuilder().setOAuthConsumerKey(twitterApiKey).setOAuthConsumerSecret(twitterSecret).build())
    var twitter            = factory.getInstance(new AccessToken(profile.getAccessToken(), profile.getAccessSecret()));
    var mentionsList       = twitter.getMentionsTimeline()

    var status = mentionsList.get(0)
    var list = List(status)
    var conversationList = conversation(list, status, twitter)
    for(status <- conversationList) {
      println(status.getText())
    }
    Ok(views.html.result(mentionsList))
  }

  private def conversation (list:List[twitter4j.Status], status:twitter4j.Status, twitter:twitter4j.Twitter):List[twitter4j.Status] = {
  var statusId = status.getInReplyToStatusId()
    if (statusId  == -1) {
      return list
    }
  var stat = twitter.showStatus(statusId)
  var conversationList = stat::list 
  conversation(conversationList, stat, twitter)
  }
}
