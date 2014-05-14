package controllers

import models.User
import models.Mention
import models.Begin
import models.Tweet
import scala.collection.JavaConversions._
import scala.collection.immutable.ListMap
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
import anorm._


object Users extends ScalaController {

  // index action
  def index = Action{ request =>
    val user = currentUser(request)
    if (user == null) {
      Redirect("/")
    }
    else{
      val begins    = Begin.findByUserId(user.id)
      val tweets    = Tweet.findByUserId(user.id)
      val countHash = countSet(tweets, user)
      Ok(views.html.users.index(user, ListMap(countHash.toSeq.sortBy(_._2).reverse:_*), begins.reverse.take(5)))
    }
  }

  // chat action
  def chat(tweet_id: String) = Action{ request =>
    val user = currentUser(request)
    if (user == null) {
      Redirect("/")
    }
    else{
      val tweets      = Tweet.findByUserId(user.id)
      val countHash   = countSet(tweets, user)
      val begin       = Begin.findByTweetId(tweet_id.toLong).get
      if(begin == null)  Redirect("/")
      val tweets_chat = Tweet.findByConversationId(begin.conversation_id)
      Ok(views.html.users.chat(user, ListMap(countHash.toSeq.sortBy(_._2).reverse:_*), begin, tweets_chat))
    }
  }

  // chats action
  def chats(twitter_id: String) = Action{ request =>
    val user = currentUser(request)
    if (user == null) {
      Redirect("/")
    }
    else{
      var tweets    = Tweet.findByUserId(user.id)
      val countHash = countSet(tweets, user)
      val begins    = Begin.findByUserId(user.id)
      if(begins.isEmpty)  Redirect("/")
      Ok(views.html.users.chats(user, ListMap(countHash.toSeq.sortBy(_._2).reverse:_*), begins.reverse))
    }
  }

  def idList() = Action{ request =>
    val user = currentUser(request)
    if (user == null) {
      Redirect("/")
    }
    else{
      var tweets    = Tweet.findByUserId(user.id)
      val countHash = countSet(tweets, user)
      Ok(views.html.users.idList(user, ListMap(countHash.toSeq.sortBy(_._2).reverse:_*)))
    }
  }

  // currentuser
  private def currentUser(request: RequestHeader) :User = {
    val sessionTwitterId   = request.session.get("twitter_id").getOrElse("")
    if (sessionTwitterId == "") {
      return null
    }
    val user = User.findByTwitterId(sessionTwitterId).get
    return user
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

  // countset
  private def countSet(tweets: Seq[Tweet], currentUser: User) : Map[String, Int] = {
    var countHash                = Map[String, Int]()
    var conversation_id:Pk[Long] = null
    println(tweets)
    for (tweet <- tweets if conversation_id != tweet.conversation_id) {
      var user_id:String = ""
      if(currentUser.twitter_id == tweet.twitter_id) {
        val begin = Begin.findByConversationId(tweet.conversation_id).get
        user_id   = begin.twitter_id
      }
      else {
        user_id   = tweet.twitter_id
      }

      if (countHash.isDefinedAt(user_id)) {
        countHash = countHash.updated(user_id, countHash(user_id)+1)
      }
      else {
        countHash = countHash.updated(user_id, 1)
      }
      conversation_id = tweet.conversation_id
    }
    return countHash
  }

  // conversation
  private def conversation (list: List[twitter4j.Status], status: twitter4j.Status, twitter: twitter4j.Twitter):List[twitter4j.Status] = {
    var statusId = status.getInReplyToStatusId()
    if (statusId == -1) {
      return list
    }
    val stat = twitter.showStatus(statusId)
    val conversationList = stat::list
    conversation(conversationList, stat, twitter)
  }
}
