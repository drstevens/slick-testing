package com.daverstevens.sql

import scalaz.std.string._
import scalaz.syntax.equal._

object DriverType {
  private val JDBCDriver = """^jdbc:(\w+):.*""".r

  /**
   * Attempts to find correct SlickDriver type for a JDBC url
   * @param url a jdbc url
   * @return
   */
  def parseUrl(url: String): Option[DriverType] = url match {
    case JDBCDriver("mysql") => Some(MySqlDriverType)
    case JDBCDriver("h2") => Some(H2DriverType)
    case _ => None

  }
}
sealed trait DriverType
case object MySqlDriverType extends DriverType
case object H2DriverType extends DriverType
