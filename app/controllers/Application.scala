package controllers

import models.User
import models.Mention
import models.Tweet
import models.Begin
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
    val user = currentUser(request)
    if (user == null){
      Ok(views.html.index(urlTwitter, user)).withSession(session)
    }
    else {
      Redirect("/users/index")
    }
  } 

  def signin = Action { request =>
    val profile = getUserProfile(request).asInstanceOf[TwitterProfile]

    if (User.findByTwitterId(profile.getUsername()).isEmpty) {
      val user: User         = new User(twitter_id    = profile.getUsername(),
                                        access_token  = profile.getAccessToken(),
                                        access_secret = profile.getAccessSecret(),
                                        image_url     = profile.getPictureUrl())
      user.insert
    }

    Redirect("/result").withSession("twitter_id"    -> profile.getUsername())
  }

  def signout = Action { request =>
    Redirect("/logout").withNewSession
  }


  def result = Action { request =>
    val twitter = twitterTokenSet(request)
    if (twitter == null) {
      Redirect("/")
    }
    else {
      val mentionsList = twitter.getMentionsTimeline()
      save_mention(mentionsList, request, twitter)
      Redirect("/users/index")
    }
  }


  def save_mention(mentionsList: twitter4j.ResponseList[twitter4j.Status], request: RequestHeader, twitter: twitter4j.Twitter) = {
    for (status <- mentionsList.reverse) {
     val conversationList = conversation(List(status), status, twitter)
     conversation_save(conversationList, request)
    }
  }

  // twitter setting
  private def twitterTokenSet(request: RequestHeader) :twitter4j.Twitter = {
    val user = currentUser(request)
    if (user == null)  {
      return null
    }
    val twitterApiKey      = Play.application.configuration.getString("twitterApiKey").get
    val twitterSecret      = Play.application.configuration.getString("twitterSecret").get
    val factory            = new TwitterFactory(new ConfigurationBuilder().setOAuthConsumerKey(twitterApiKey).setOAuthConsumerSecret(twitterSecret).build())
    val twitter            = factory.getInstance(new AccessToken(user.access_token, user.access_secret))
    return twitter
  }

  //currentuser
  private def currentUser(request: RequestHeader) :User = {
    val sessionTwitterId = request.session.get("twitter_id").getOrElse("")
    if (sessionTwitterId == "") {
      return null
    }
    val user               = User.findByTwitterId(sessionTwitterId).get
    return user
  }

  //conversation
  private def conversation (list: List[twitter4j.Status], status: twitter4j.Status, twitter: twitter4j.Twitter):List[twitter4j.Status] = {
    var statusId = status.getInReplyToStatusId()
    if (statusId == -1) {
      return list
    }
    val stat = twitter.showStatus(statusId)
    val conversationList = stat::list
    conversation(conversationList, stat, twitter)
  }

  private def conversation_save (conversationList: List[twitter4j.Status], request:RequestHeader) = {
    var begin:Begin = null
    for (status <- conversationList) {
      if(status.getInReplyToStatusId() == -1){
        begin = begin_save(status, request)
      }
      else {
        tweet_save (status, begin, request)
      }
    }
  }

  private def begin_save (status: twitter4j.Status, request:RequestHeader) :Begin = {
    var begin_option = Begin.findByTweetId(status.getId())
    if (begin_option.isEmpty) {
      var begin = new Begin(user_id         = currentUser(request).id,
                            image_url       = status.getUser().getProfileImageURL(),
                            twitter_id      = status.getUser().getScreenName(),
                            text            = status.getText(),
                            time            = status.getCreatedAt().getTime().toLong,
                            tweet_id        = status.getId()
                            )
      begin.insert
      begin = Begin.findByTweetId(status.getId()).get
      return begin
    }
    else {
      val begin = begin_option.get
      return begin
    }
  }

  private def tweet_save (status: twitter4j.Status, begin:Begin, request:RequestHeader) = {
    var tweet_option = Tweet.findByTweetId(status.getId())
    if (tweet_option.isEmpty) {
      val tweet = new Tweet(user_id         = currentUser(request).id,
                            image_url       = status.getUser().getProfileImageURL(),
                            twitter_id      = status.getUser().getScreenName(),
                            text            = status.getText(),
                            time            = status.getCreatedAt().getTime().toLong,
                            tweet_id        = status.getId(),
                            reply           = status.getInReplyToStatusId(),
                            conversation_id = begin.conversation_id
                            )
      tweet.insert
    }
  }
} 
