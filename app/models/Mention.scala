package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class Mention (
  id: Pk[Long] = NotAssigned,
  user_id: Pk[Long], twitter_id: String, image_url: String, mention_id: Long, tweet_text: String
)

object Mention {

  val data = {
    get[Pk[Long]]("mention.id") ~
    get[Pk[Long]]("mention.user_id") ~
    get[String]("mention.twitter_id") ~
    get[String]("mention.image_url") ~
    get[Long]("mention.mention_id") ~
    get[String]("mention.tweet_text") map {
      case id ~ user_id ~ twitter_id ~ image_url ~ mention_id ~ tweet_text 
      => Mention(id, user_id, twitter_id, image_url, mention_id, tweet_text)
    }
  }

  def insert(mention: Mention) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into Mention(user_id, twitter_id, image_url, mention_id, tweet_text)
          values ({user_id}, {twitter_id}, {image_url}, {mention_id}, {tweet_text})
        """
      ).on(
        'user_id    -> mention.user_id,
        'twitter_id -> mention.twitter_id,
        'image_url  -> mention.image_url,
        'mention_id -> mention.mention_id,
        'tweet_text -> mention.tweet_text
      ).executeUpdate()
    }
  }

  def findByUserId(user_id: Pk[Long]): Seq[Mention] = {
    DB.withConnection { implicit connection => 
      SQL("select * from Mention where user_id = {user_id}").on('user_id -> user_id).as(Mention.data *)
    }
  }

  def findByMentionId(mention_id: Long): Seq[Mention] = {
    DB.withConnection { implicit connection => 
      SQL("select * from Mention where mention_id = {mention_id}").on('mention_id -> mention_id).as(Mention.data *)
    }
  }
}
