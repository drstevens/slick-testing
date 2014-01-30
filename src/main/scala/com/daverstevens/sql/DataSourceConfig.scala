package com.daverstevens
package sql

import com.typesafe.config.Config
import com.jolbox.bonecp.{BoneCPDataSource, BoneCPConfig}
import com.zaxxer.hikari.{HikariDataSource, HikariConfig}

object DataSourceConfig {
  def fromConfig(cfg: Config): DataSourceConfig = {
    val url = cfg.getString("url")
    val user = cfg.getString("user")
    val password = cfg.getString("password")
    val driverType = DriverType.parseUrl(url)
        .fold[DriverType](throw new Exception(s"'$url' does not represent a valid driver"))(identity)

    cfg.getString("pool-type") match {
      case BoneCPDataSourceConfig.name =>
        BoneCPDataSourceConfig(
          driverType = driverType,
          url = url,
          username = user,
          password = password)
      case HikariCPDataSourceConfig.name =>
        HikariCPDataSourceConfig(
          driverType = driverType,
          className = cfg.getString("driver"),
          url = url,
          username = user,
          password = password)
      case invalidPoolType => throw new Exception(s"'$invalidPoolType' is not a valid pool type")
    }
  }
}

sealed trait DataSourceConfig {
  def driverType: DriverType
  def newSqlClient(): SqlClient
}

case class HikariCPDataSourceConfig(driverType: DriverType, className: String, url: String, username: String, password: String) extends DataSourceConfig {
  override def toString: String =
    s"HikariCPDataSourceConfig(driverType: $driverType, className: $className, url: $url, username: $username, password: *****)"

  override def newSqlClient(): SqlClient = {
    Class.forName(className) //make sure underlying datasource made the cut
    val dsConfig = new HikariConfig()

    //For config options see https://github.com/brettwooldridge/HikariCP
    dsConfig.setMaximumPoolSize(100)
    dsConfig.setDataSourceClassName(className)
    dsConfig.addDataSourceProperty("url", url)
    dsConfig.addDataSourceProperty("user", username)
    dsConfig.addDataSourceProperty("password", password)
    dsConfig.setIdleTimeout(600000) //10 minutes
    dsConfig.setAcquireIncrement(1)
    val datasource = new HikariDataSource(dsConfig)
    new SqlClient(datasource, () => datasource.shutdown())
  }
}
object HikariCPDataSourceConfig {
  val name = "hikaricp"
}

case class BoneCPDataSourceConfig(driverType: DriverType, url: String, username: String, password: String) extends DataSourceConfig {
  override def toString: String =
    s"BoneCPDataSourceConfig(driverType: $driverType, url: $url, username: $username, password: *****)"

  def newSqlClient(): SqlClient = {
    val dsConfig = new BoneCPConfig()

    dsConfig.setJdbcUrl(url)
    dsConfig.setUsername(username)
    dsConfig.setPassword(password)
    dsConfig.setIdleConnectionTestPeriodInMinutes(10)
    dsConfig.setAcquireIncrement(1)
    dsConfig.setDisableJMX(true)
    val datasource = new BoneCPDataSource(dsConfig)
    new SqlClient(datasource, () => datasource.close())
  }
}
object BoneCPDataSourceConfig {
    val name = "bonecp"
}
