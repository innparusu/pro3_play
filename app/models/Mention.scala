package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class Mention (
  id: Pk[Long] = NotAssigned,
  twitter_id: String, mention_id: Long
)

object Mention {

  val data = {
    get[Pk[Long]]("mention.id") ~
    get[String]("mention.twitter_id") ~
    get[Long]("mention.mention_id") map {
      case id ~ twitter_id ~ mention_id => Mention(id, twitter_id, mention_id)
    }
  }

  def insert(mention: Mention) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into mention(twitter_id, mention_id)
          values ({twitter_id}, {mention_id})
        """
      ).on(
        'twitter_id -> mention.twitter_id,
        'mention_id -> mention.mention_id
      ).executeUpdate()
    }
  }

  def findByTwitterId(twitter_id: String): Seq[Mention] = {
    DB.withConnection { implicit connection => 
      SQL("select * from mention where twitter_id = {twitter_id}").on('twitter_id -> twitter_id).as(Mention.data *)
    }
  }

  def findByMentionId(mention_id: Long): Seq[Mention] = {
    DB.withConnection { implicit connection => 
      SQL("select * from mention where mention_id = {mention_id}").on('mention_id -> mention_id).as(Mention.data *)
    }
  }
}
