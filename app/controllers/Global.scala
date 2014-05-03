import play.api._
import play.api.Play.current
import play.api.mvc._
import org.pac4j.core.client.Clients
import org.pac4j.oauth.client.TwitterClient
import org.pac4j.play.Config

object Global extends GlobalSettings {
  override def onStart(app:Application) {
    val twitterClient = new TwitterClient("OEccWOt0t1FyfY5stIYKECME6", "ADqXLYdVMZkjf2yv4qhEpJGCau9pwLhmmzJOMFyU6im9XYX2IM")
    val clients = new Clients("http://localhost:9000/callback",twitterClient)
    Config.setClients(clients)
  }
}
