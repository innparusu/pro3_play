package models

import play.api.db._
import anorm._
import play.api.Play.current
import anorm.SqlParser._

case class Begin(
  conversation_id:Pk[Long] = NotAssigned,
  user_id:        Pk[Long],
  image_url:      String,
  twitter_id:     String,
  text:           String,
  time:           Long,
  tweet_id:       Long) {

  def insert = {
    DB.withConnection { implicit c =>
      SQL(
        """
        insert into begin (user_id, image_url, twitter_id, text, time, tweet_id)
        values ({user_id}, {image_url}, {twitter_id}, {text}, {time}, {tweet_id})
        """
      ).on(
        'user_id        -> this.user_id,
        'image_url      -> this.image_url,
        'twitter_id     -> this.twitter_id,
        'text           -> this.text,
        'time           -> this.time,
        'tweet_id       -> this.tweet_id
      ).executeUpdate()
    }
  }
}


object Begin {

  val data = {
    get[Pk[Long]]("begin.conversation_id") ~
    get[Pk[Long]]("begin.user_id") ~
    get[String]("begin.image_url") ~
    get[String]("begin.twitter_id") ~
    get[String]("begin.text") ~
    get[Long]("begin.time") ~
    get[Long]("begin.tweet_id") map {
      case conversation_id ~ user_id ~ image_url ~ twitter_id ~ text ~ time ~ tweet_id 
      => Begin(conversation_id, user_id, image_url, twitter_id, text, time, tweet_id)
    }
  }

  def findByConversationId(conversation_id: Pk[Long]): Option[Begin] = {
    DB.withConnection { implicit connection =>
      SQL("select * from begin where conversation_id = {conversation_id}").on('conversation_id -> conversation_id).as(Begin.data.singleOpt)
    }
  }

  def findByUserId(user_id: Pk[Long]): Seq[Begin] = {
    DB.withConnection { implicit connection => 
      SQL("select * from begin where user_id = {user_id}").on('user_id -> user_id).as(Begin.data *)
    }
  }

  def findByTweetId(tweet_id: Long): Option[Begin] = {
    DB.withConnection { implicit connection =>
      SQL("select * from begin where tweet_id = {tweet_id}").on('tweet_id -> tweet_id).as(Begin.data.singleOpt)
    }
  }
}
