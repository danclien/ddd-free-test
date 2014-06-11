package schoolobjects.framework.testing

import schoolobjects.framework.configuration._
import schoolobjects.framework.database._

trait IntegrationSpec {
  // This should be moved somewhere else?
  val config = Configuration(
    "jdbc:postgresql://localhost:15432/schoolobjects",
    "schoolobjects",
    "schoolobjects"
  )

  val database = DatabaseFactory.createDatabaseFromConfiguration(config)
}
