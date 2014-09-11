package com.daverstevens.model

case class GroupId(value: Long)
case class RoleId(value: Long)

case class Group(id: GroupId, name: String)
case class Role(id: RoleId, name: String)
case class GroupRole(groupId: GroupId, roleId: RoleId)
