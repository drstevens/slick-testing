package com.daverstevens
package sql

import com.daverstevens.model._

object H2SqlModel extends SqlModel with H2SlickDriver
object MySqlModel extends SqlModel with MySqlSlickDriver
object SqlModel {
  val fromDriverType: DriverType => SqlModel = {
    case MySqlDriverType => MySqlModel
    case H2DriverType => H2SqlModel
  }
}

sealed trait SqlModel extends SlickDriver {
  import driver.simple._

  object implicitMappings {
    implicit def groupIdMapper = MappedColumnType.base[GroupId, Long](_.value,v => GroupId(v))
    implicit def roleIdMapper = MappedColumnType.base[RoleId, Long](_.value,v => RoleId(v))
  }

  import implicitMappings._

  private lazy val ddl = groupTable.ddl ++ roleTable.ddl ++ groupRoleTable.ddl

  def createStatements: List[String] =
    ddl.createStatements.map(_ + ";").toList

  def createTables()(implicit session: Session): Unit = ddl.create

  def dropTables()(implicit session: Session): Unit = ddl.drop

  class GroupTable(tag: Tag) extends Table[(GroupId, String)](tag, "Group") {
    def id = column[GroupId]("ID", O.AutoInc, O.PrimaryKey)
    def name = column[String]("NAME", O.NotNull)

    def * = (id, name)
  }
  object groupTable extends TableQuery[GroupTable](new GroupTable(_)) {
    def foo(id: GroupId) = groupTable.filter(_.id === id)
    def insertAndGet(name: String)(implicit s: Session): Group = Group((groupTable returning groupTable.map(_.id)).insert(GroupId(1l), name), name)
  }

  class RoleTable(tag: Tag) extends Table[(RoleId, String)](tag, "Role") {
    def id = column[RoleId]("ID", O.AutoInc, O.PrimaryKey)
    def name = column[String]("NAME", O.NotNull)

    def * = (id, name)
  }
  object roleTable extends TableQuery[RoleTable](new RoleTable(_)) {
    def foo(id: RoleId) = roleTable.filter(_.id === id)
  }

  class GroupRoleTable(tag: Tag) extends Table[(GroupId, RoleId)](tag, "GroupRole") {
    def groupId = column[GroupId]("GROUP_ID", O.NotNull)
    def roleId = column[RoleId]("ROLE_ID", O.NotNull)

    def pk = primaryKey("PK_GROUPROLE", (groupId, roleId))
    def groupFK = foreignKey("FK_GROUPROLE_GROUP", groupId, groupTable)(_.id)
    def roleFK = foreignKey("FK_GROUPROLE_ROLE", roleId, roleTable)(_.id)

    def * = (groupId, roleId)
  }
  object groupRoleTable extends TableQuery[GroupRoleTable](new GroupRoleTable(_)) {
    def byGroupId(id: GroupId) = groupRoleTable.filter(_.groupId === id)
    def byRoleId(id: RoleId) = groupRoleTable.filter(_.roleId === id)
  }

}
