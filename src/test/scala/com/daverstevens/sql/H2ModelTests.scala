package com.daverstevens.sql

import scala.slick.lifted.StringColumnExtensionMethods
import org.specs2.mutable.Specification
import com.daverstevens.model._

class H2ModelTests extends Specification with H2Testing {

  "Insert some data" ! usingH2DB {
    db => implicit model => db.withSession { implicit s =>
      val exGroup1 = model.groupTable.insertAndGet(GroupName("group1"), "1", 1)
      val exGroup2 = model.groupTable.insertAndGet(GroupName("group2"), "2", 2)
      val exGroup3 = model.groupTable.insertAndGet(GroupName("group3"), "3", 1)

      import model.driver.simple._
      import model.implicitMappings._

      val group1 = model.groupTable.filter(_.id === exGroup1.id).firstOption
      val group2 = model.groupTable.filter(_.id === exGroup2.id).firstOption
      val group3 = model.groupTable.filter(_.id === exGroup3.id).firstOption

      exGroup1.name ==== GroupName("group1") and
      exGroup2.name ==== GroupName("group2") and
      exGroup3.name ==== GroupName("group3") and
      (exGroup1.id !=== exGroup2.id) and
      (exGroup2.id !=== exGroup3.id) and
      group1 ==== Some(exGroup1)
      group2 ==== Some(exGroup2)
      group3 ==== Some(exGroup3)
    }
  }

  "Use extensions" ! usingH2DB {
    db => implicit model => db.withSession { implicit s =>
      val exGroup1 = model.groupTable.insertAndGet(GroupName("group1"), "str", 10)

      import model.driver.simple._
      import model.implicitMappings._

      //implicit def stringColumnExtensionMethods(c: Column[GroupName]) = new StringColumnExtensionMethods[GroupName](c)
      // Uncommenting above line results in the following error
      // [error] /Users/dave/workspace/slick-testing/src/test/scala/com/daverstevens/sql/H2ModelTests.scala:42: Cannot perform option-mapped operation
      // [error]       with type: (com.daverstevens.model.GroupName, com.daverstevens.model.GroupName) => R
      // [error]   for base type: (String, String) => Boolean

      // Ultimately I would like to define a case class PartialGroupName and use this instead but first things first
      val group1 = model.groupTable.filter(_.name like GroupName("%group1%")).firstOption

      exGroup1.name ==== GroupName("group1") and
        group1 ==== Some(exGroup1)
    }
  }

  "Insert some more data" ! usingH2DB {
    db => implicit model => db.withSession { implicit s =>
      val exGroup1 = model.groupTable.insertAndGet(GroupName("group1"), "1", 1)
      val exGroup2 = model.groupTable.insertAndGet(GroupName("group2"), "2", 2)
      val exGroup3 = model.groupTable.insertAndGet(GroupName("group3"), "3", 1)

      import model.driver.simple._
      import model.implicitMappings._

      val groups = model.groupTable
        .filter(g => g.id === exGroup1.id || g.id === exGroup2.id || g.id === exGroup3.id)
        .list
      groups must containAllOf(List(exGroup1, exGroup2, exGroup3))
    }
  }


}
