import play.api._
import play.api.Play.current
import play.api.mvc._
import org.pac4j.core.client.Clients
import org.pac4j.oauth.client.TwitterClients
import org.pac4j.play.Config

object Global extends GlobalSettings {
  override def onStart(app:Application) {
    val twitterClient = new TwitterClients()
    val clients = new Clients("http://localhost:9000/callback",twitterClient)
    Config.setClients(clients)
  }
}
