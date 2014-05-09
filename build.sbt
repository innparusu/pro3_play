name := "twitter_app"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.pac4j"     % "play-pac4j_scala" % "1.2.0",
  "org.pac4j"     % "pac4j-http" % "1.5.0",
  "org.pac4j"     % "pac4j-cas" % "1.5.0",
  "org.pac4j"     % "pac4j-openid" % "1.5.0",
  "org.pac4j"     % "pac4j-oauth" % "1.5.0",
  "org.pac4j"     % "pac4j-saml" % "1.5.0",
  "org.twitter4j" % "twitter4j-core" % "4.0.1",
  "mysql"         % "mysql-connector-java" % "5.1.18"
) 

play.Project.playScalaSettings
