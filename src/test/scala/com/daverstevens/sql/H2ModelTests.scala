package com.daverstevens.sql

import org.specs2.mutable.Specification
import com.daverstevens.model.Group


class H2ModelTests extends Specification with H2Testing {

  "Insert some data" ! usingH2DB {
    db => implicit model => db.withSession { implicit s =>
      val exGroup1 = model.groupTable.insertAndGet("group1")
      val exGroup2 = model.groupTable.insertAndGet("group2")
      val exGroup3 = model.groupTable.insertAndGet("group3")

      import model.driver.simple._
      import model.implicitMappings._

      val group1 = model.groupTable.filter(_.id === exGroup1.id).firstOption().map(Group.tupled)
      val group2 = model.groupTable.filter(_.id === exGroup2.id).firstOption().map(Group.tupled)
      val group3 = model.groupTable.filter(_.id === exGroup3.id).firstOption().map(Group.tupled)

      exGroup1.name ==== "group1" and
      exGroup2.name ==== "group2" and
      exGroup3.name ==== "group3" and
      (exGroup1.id !=== exGroup2.id) and
      (exGroup2.id !=== exGroup3.id) and
      group1 ==== Some(exGroup1)
      group2 ==== Some(exGroup2)
      group3 ==== Some(exGroup3)
    }
  }

  "Insert some more data" ! usingH2DB {
    db => implicit model => db.withSession { implicit s =>
      val exGroup1 = model.groupTable.insertAndGet("group1")
      val exGroup2 = model.groupTable.insertAndGet("group2")
      val exGroup3 = model.groupTable.insertAndGet("group3")

      import model.driver.simple._
      import model.implicitMappings._

      val groups = model.groupTable
        .filter(g => g.id === exGroup1.id || g.id === exGroup2.id || g.id === exGroup3.id)
        .list().map(Group.tupled)
      groups must containAllOf(List(exGroup1, exGroup2, exGroup3))
    }
  }


}
