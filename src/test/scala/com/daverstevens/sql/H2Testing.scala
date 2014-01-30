package com.daverstevens.sql

import com.jolbox.bonecp.{BoneCPDataSource, BoneCPConfig}
import scala.slick.jdbc.JdbcBackend.Database
import scala.util.Random

trait H2Testing {

  def newDataSource(): BoneCPDataSource = {
    val dsConfig = new BoneCPConfig()
    dsConfig.setJdbcUrl(s"jdbc:h2:mem:testing${Random.alphanumeric}") //create new db per test
    dsConfig.setUsername("user")
    dsConfig.setPassword("password")
    dsConfig.setIdleConnectionTestPeriodInMinutes(10)
    dsConfig.setAcquireIncrement(1)
    dsConfig.setDisableJMX(true)
    new BoneCPDataSource(dsConfig)
  }

  def usingH2DB[T](test: Database => SqlModel => T) = {
    val ds = newDataSource()
    val model = H2SqlModel
    try {
      val db = Database.forDataSource(ds)
      db.withSession(model.createTables()(_))
      test(db)(model)
    } finally {
      ds.close()
    }
  }


}
