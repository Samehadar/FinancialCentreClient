package com.samehadar.financialcentre

import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest}

/**
  * Created by vital on 28.01.2018.
  */
class Methods(http: HttpExt, rootPath: String) {
  def getBalance(userInfoId: Long, currency: Currency, accountType: AccountType): Option[Currency] = {
    val req = HttpRequest(uri = s"$rootPath/balance")
    None
  }

  def getBalances(userInfoId: Long): Map[AccountType,Currency] = {
    val req = HttpRequest(uri = s"$rootPath/balances")
    Map.empty
  }

  def transact(from: Account, to: Account, currency: Currency, documentId: String): Either[Err,Unit] = {
    val str = ""

    val req = HttpRequest(
      method = HttpMethods.POST,
      uri = s"$rootPath/balance",
      entity = HttpEntity(str)
    )

    Left(Err2)
  }
}

object Methods {
  def apply(http: HttpExt, rootPath: String): Methods = new Methods(http, rootPath)
}