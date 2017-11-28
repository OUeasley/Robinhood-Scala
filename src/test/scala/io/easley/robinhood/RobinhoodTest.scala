package io.easley.robinhood

import org.scalatest.{FunSuite, Outcome}

import scala.concurrent.duration._
import org.scalatest._

import scala.concurrent.Await

class RobinhoodTest extends FunSuite {
  val robinhood = new Robinhood

  test("Robinhood should get user account token ") {
//    Await.result(robinhood.login(), 10 seconds)
//    val ip = Await.result(robinhood.getInvestmentProfile(), 10 seconds)
//    println(ip)
//    assert(robinhood.isUserLoggedIn(), true)
//    assert(!robinhood.getToken().isEmpty, true)
  }

}
