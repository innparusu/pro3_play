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

object Application extends ScalaController {

  def index = Action { request =>
    val session = getOrCreateSessionId(request)
    val urlTwitter = getRedirectAction(request, session, "TwitterClient", "/signin").getLocation()
    Ok(views.html.index(urlTwitter, currentUser(request))).withSession(session)
  } 

  def signin = Action { request =>
    val profile = getUserProfile(request).asInstanceOf[TwitterProfile]

    if (User.findByTwitterId(profile.getUsername()).isEmpty) {
      val user: User         = new User(twitter_id    = profile.getUsername(),
                                        access_token  = profile.getAccessToken(),
                                        access_secret = profile.getAccessSecret())
      User.insert(user)
    }

    Redirect("/result").withSession("twitter_id"    -> profile.getUsername())
  }

  def signout = Action { request =>
    Redirect("/logout").withNewSession
  }


  def result = Action { request =>
    val twitter      = twitterTokenSet(request)
    if (twitter == null) {
      Redirect("/")
    }

    else {
      val mentionsList = twitter.getMentionsTimeline()
      save_mention(mentionsList, request)
      Redirect("/users/index")
    }
  }


  def save_mention(mentionsList: twitter4j.ResponseList[twitter4j.Status], request: RequestHeader) = {
    for (status <- mentionsList.reverse) {
      if (Mention.findByMentionId(status.getId).isEmpty) {
        val mention :Mention         = new Mention(twitter_id = currentUser(request).twitter_id,
                                                   mention_id = status.getId())
        Mention.insert(mention)
      }
    }
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
}
