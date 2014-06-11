package schoolobjects.framework.configuration

case class Configuration(
  databaseUrl: String,
  databaseUsername: String,
  databasePassword: String
) {

  val databaseDriver = ("org.postgresql.Driver")
}