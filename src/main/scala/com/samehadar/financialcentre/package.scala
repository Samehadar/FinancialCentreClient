package com.samehadar

package object financialcentre {
  import io.circe._, io.circe.generic.semiauto._

  sealed trait AccountType
  case object Internal
  case object Trade
  case object Tax

  sealed trait Currency
  case class Dollar(value: Int) extends Currency
  case class Euro(value: Int) extends Currency
  case class Ruble(value: Int) extends Currency

  case class Account(userInfoId: Long, accountType  : AccountType)

  sealed trait Err
  case class NetworkError(t: Throwable) extends Err
  case class DecodingError(e: Error) extends Err
  case object Err2 extends Err


  implicit val currencyDecoder = deriveDecoder[Currency]
  implicit val accountTypeDecoder = deriveDecoder[Currency]
}
