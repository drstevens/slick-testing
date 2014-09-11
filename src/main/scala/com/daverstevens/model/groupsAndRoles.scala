package com.daverstevens.model

import scalaz.{Store, @>, Lens}

case class GroupId(value: Long)
case class RoleId(value: Long)

case class GroupName(value: String)
case class Group(id: GroupId, name: GroupName, someString: String, someInt: Int)
object Group {
  val idLens: Group @> GroupId = Lens(g => Store(gId => g.copy(id = gId), g.id))
}
case class RoleName(value: String)
case class Role(id: RoleId, name: RoleName)
case class GroupRole(groupId: GroupId, roleId: RoleId)
