import play.api._
import play.api.Play.current
import play.api.mvc._
import org.pac4j.core.client.Clients
import org.pac4j.oauth.client.TwitterClient
import org.pac4j.play.Config

object Global extends GlobalSettings {
  override def onStart(app:Application) {
    val twitterApiKey = Play.application.configuration.getString("twitterApiKey").get
    val twitterSecret = Play.application.configuration.getString("twitterSecret").get
    val twitterClient = new TwitterClient(twitterApiKey,twitterSecret)
    val clients = new Clients("http://localhost:9000/callback",twitterClient)
    Config.setClients(clients)
  }
}
