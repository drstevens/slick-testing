package com.daverstevens
package sql

import scala.slick.driver.{H2Driver, MySQLDriver, JdbcDriver}

trait SlickDriver {
  val driver: JdbcDriver
}

trait MySqlSlickDriver extends SlickDriver {
  val driver = MySQLDriver
}

trait H2SlickDriver extends SlickDriver {
  val driver = H2Driver
}
