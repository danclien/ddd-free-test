package schoolobjects.framework.database

import schoolobjects.framework.configuration._
import com.jolbox.bonecp.BoneCPDataSource
import scala.slick.jdbc.JdbcBackend._

object DatabaseFactory {
  def createDatabase(driver: String, url: String, username: String, password: String): Database = {
    val ds = new BoneCPDataSource()

    ds.setDriverClass(driver)
    ds.setJdbcUrl(url)
    ds.setUsername(username)
    ds.setPassword(password)

    ds.setPartitionCount(1)
    ds.setMinConnectionsPerPartition(2)
    ds.setMaxConnectionsPerPartition(2)
    ds.setDefaultAutoCommit(true)

    Database.forDataSource(ds)
  }

  def createDatabaseFromConfiguration(c: Configuration): Database = {
    createDatabase(c.databaseDriver, c.databaseUrl, c.databaseUsername, c.databasePassword)
  }
}