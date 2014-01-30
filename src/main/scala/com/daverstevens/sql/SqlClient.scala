package com.daverstevens
package sql

import javax.sql.DataSource
import scala.slick.jdbc.JdbcBackend._

class SqlClient(dataSource: DataSource, shutdownDataSource: () => Unit) {
  val database: Database = Database.forDataSource(dataSource)

  def shutdown(): Unit = {
    shutdownDataSource()
  }
}