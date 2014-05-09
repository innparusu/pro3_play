package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class User (
  id: Pk[Long] = NotAssigned,
  twitter_id: String, access_token: String, access_secret: String
)

object User {

  val data = {
    get[Pk[Long]]("user.id") ~
    get[String]("user.twitter_id") ~
    get[String]("user.access_token") ~
    get[String]("user.access_secret") map {
      case id ~ twitter_id ~ access_token ~ access_secret => User(id, twitter_id, access_token, access_secret)
    }
  }

  def insert(user: User) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into user(twitter_id, access_token, access_secret)
          values ({twitter_id}, {access_token}, {access_secret})
        """
      ).on(
        'twitter_id   -> user.twitter_id,
        'access_token -> user.access_token,
        'access_secret -> user.access_secret
      ).executeUpdate()
    }
  }
 
  def findById(id: Long): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where id = {id}").on('id -> id).as(User.data.singleOpt)
    }
  }

  def findByTwitterId(twitter_id: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where twitter_id = {twitter_id}").on('twitter_id -> twitter_id).as(User.data.singleOpt)
    }
  }
 
  def findAll(): Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user").as(User.data *)
    }
  }
}
