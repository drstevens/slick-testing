package com.daverstevens.sql

import org.specs2.mutable.Specification

class MySqlModelTests extends Specification {

  val model = MySqlModel

  "MySql generates valid ddl" ! {
    MySqlModel.createStatements !=== H2SqlModel.createStatements
  }

}
