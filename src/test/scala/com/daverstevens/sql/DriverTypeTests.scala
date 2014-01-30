package com.daverstevens.sql

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification
import org.scalacheck.{Gen, Prop}

class DriverTypeTests extends Specification with ScalaCheck {

  def urlsAndDriverType = for {
    (driverName, driverType) <- Gen.oneOf[(String, Option[DriverType])](
      Gen.const("mysql" -> Some(MySqlDriverType)),
      Gen.const("h2" -> Some(H2DriverType)),
      Gen.alphaStr.map(_ -> None))
    host <- Gen.alphaStr
    url = s"jdbc:$driverName:$host"
  } yield url -> driverType

  "urls should map to correct drivers" ! Prop.forAllNoShrink(urlsAndDriverType) {
    case (url, driverTypeOption) =>
      DriverType.parseUrl(url) ==== driverTypeOption
  }

}
