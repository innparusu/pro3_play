package models

import play.api.db._
import anorm._
import play.api.Play.current
import anorm.SqlParser._

case class Tweet (
  id:             Pk[Long] = NotAssigned,
  user_id:        Pk[Long],
  image_url:      String,
  twitter_id:     String,
  text:           String,
  time:           Long,
  tweet_id:       Long,
  reply:          Long,
  conversation_id:Pk[Long]
) {
  def insert = {
    DB.withConnection { implicit c =>
      SQL(
        """
        insert into tweet (user_id, image_url, twitter_id, text, time, tweet_id, reply, conversation_id)
        values ({user_id}, {image_url}, {twitter_id}, {text}, {time}, {tweet_id}, {reply}, {conversation_id})
        """
      ).on(
        'user_id         -> this.user_id,
        'image_url       -> this.image_url,
        'twitter_id      -> this.twitter_id,
        'text            -> this.text,
        'time            -> this.time,
        'tweet_id        -> this.tweet_id,
        'reply           -> this.reply,
        'conversation_id -> this.conversation_id
      ).executeUpdate()
    }
  }
}


object Tweet {

  val data = {
    get[Pk[Long]]("tweet.id") ~
    get[Pk[Long]]("tweet.user_id") ~
    get[String]("tweet.image_url") ~
    get[String]("tweet.twitter_id") ~
    get[String]("tweet.text") ~
    get[Long]("tweet.time") ~
    get[Long]("tweet.tweet_id") ~
    get[Long]("tweet.reply") ~
    get[Pk[Long]]("tweet.conversation_id") map {
      case id ~ user_id ~ image_url ~ twitter_id ~ text ~ time ~ tweet_id ~ reply ~ conversation_id 
      => Tweet(id, user_id, image_url, twitter_id, text, time, tweet_id, reply, conversation_id)
    }
  }

  def findById(id: Pk[Long]): Option[Tweet] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tweet where id = {id}").on('id -> id).as(Tweet.data.singleOpt)
    }
  }

  def findByUserId(user_id: Pk[Long]): Seq[Tweet] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tweet where user_id = {user_id}").on('user_id -> user_id).as(Tweet.data *)
    }
  }

  def findByTwitterId(twitter_id: String): Seq[Tweet] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tweet where twitter_id = {twitter_id}").on('twitter_id -> twitter_id).as(Tweet.data *)
    }
  }

  def findByConversationId(conversation_id: Pk[Long]): Seq[Tweet] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tweet where conversation_id = {conversation_id}").on('conversation_id -> conversation_id).as(Tweet.data *)
    }
  }

  def findByTweetId(tweet_id: Long): Option[Tweet] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tweet where tweet_id = {tweet_id}").on('tweet_id -> tweet_id).as(Tweet.data.singleOpt)
    }
  }
}
