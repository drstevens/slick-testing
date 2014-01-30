package com.daverstevens
package app

import java.io.File
import com.typesafe.config.ConfigFactory
import scala.slick.util.Logging
import scala.slick.jdbc.JdbcBackend.Session
import com.daverstevens.sql.{SqlModel, MySqlModel, DataSourceConfig}

case class Configuration(dataSource: DataSourceConfig)
object Configuration {
  def fromPath(path: String): Configuration = {
    val config = ConfigFactory.parseFile(new File(path)).resolve()
    config.checkValid(ConfigFactory.empty())
    val dsConfig = config.getConfig("db")
    Configuration(DataSourceConfig.fromConfig(dsConfig))
  }
}

object SlickTesting extends App with Logging {
  sealed trait Cmd
  case object DDL extends Cmd
  case object CreateTables extends Cmd
  case object DropTables extends Cmd
  case object DropCreate extends Cmd
  case class Input(cmd: Option[Cmd], cfg: Option[DataSourceConfig])

  val parser = new scopt.OptionParser[Input]("DBSetup") {
    head("DB Setup Utility")
    opt[String]('p', "path") required() action {
      (p, input) =>
        logger.info(s"Using config path $p")
        input.copy(cfg = Some(app.Configuration.fromPath(p).dataSource))
    } text "path required."
    cmd("DDL") action {
      (_, input) => input.copy(cmd = Some(DDL)) }
    cmd("CreateTables") action {
      (_, input) => input.copy(cmd = Some(CreateTables)) }
    cmd("DropTables") action {
      (_, input) => input.copy(cmd = Some(DropTables)) }
    cmd("DropCreate") action {
      (_, input) => input.copy(cmd = Some(DropTables)) }
    checkConfig {
      i => if (i.cmd.isEmpty) failure("Cmd must be provided") else success
    }
  }

  def execute[T](cfg: DataSourceConfig)(f: SqlModel => Session => T): T = {
    val sqlClient = cfg.newSqlClient()
    try {
      logger.info("Attempting to create tables in DB")
      sqlClient.database.withSession(f(SqlModel.fromDriverType(cfg.driverType)))
    } finally {
      sqlClient.shutdown()
    }
  }

  parser.parse(args, Input(None, None)).foreach{
    //Generate DDL
    case Input(Some(DDL), Some(cfg)) =>
      logger.info("Attempting to generate DDL for schema")
      MySqlModel.createStatements.foreach(println)

    case Input(Some(CreateTables), Some(cfg)) => execute(cfg) {
      model => implicit s => model.createTables()
    }

    case Input(Some(DropTables), Some(cfg)) => execute(cfg) {
      model => implicit s => model.dropTables()
    }

    case Input(Some(DropCreate), Some(cfg)) => execute(cfg) {
      model => implicit s =>
        model.dropTables()
        model.createTables()
    }


    case _ => logger.error("This should not have happened!")
  }

}
