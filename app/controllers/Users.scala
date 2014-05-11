package controllers

import models.User
import models.Mention
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

object Users extends ScalaController {

  def index = Action{ request =>
    val user = currentUser(request)
    if (user == null) {
      Redirect("/")
    }
    else{
      Ok(views.html.users.index(user))
    }
  }

  // twitter setting
  def twitterTokenSet(request: RequestHeader) :twitter4j.Twitter = {
    val user = currentUser(request)
    if (user == null)  {
      return null
    }
    val twitterApiKey      = Play.application.configuration.getString("twitterApiKey").get
    val twitterSecret      = Play.application.configuration.getString("twitterSecret").get
    val factory            = new TwitterFactory(new ConfigurationBuilder().setOAuthConsumerKey(twitterApiKey).setOAuthConsumerSecret(twitterSecret).build())
    val twitter            = factory.getInstance(new AccessToken(user.access_token, user.access_secret))
    twitter
  }

  //currentuser
  def currentUser(request: RequestHeader) :User = {
    val sessionTwitterId   = request.session.get("twitter_id").getOrElse("")
    if (sessionTwitterId == "") {
      return null
    }
    val user               = User.findByTwitterId(sessionTwitterId).get
    return user
  }

  private def conversation (list: List[twitter4j.Status], status: twitter4j.Status, twitter: twitter4j.Twitter):List[twitter4j.Status] = {
    var statusId = status.getInReplyToStatusId()
    if (statusId == -1) {
      return list
    }
  var stat = twitter.showStatus(statusId)
  var conversationList = stat::list
  conversation(conversationList, stat, twitter)
  }
}
