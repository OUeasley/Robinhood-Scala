package io.easley.robinhood

import org.scalatest.{FunSuite, Outcome}

import scala.concurrent.duration._
import org.scalatest._

import scala.concurrent.Await

class RobinhoodTest extends FunSuite {
  val robinhood = new Robinhood

  test("Robinhood should get user account token ") {

//    val res =
//      Await.result(robinhood.placeBuyOrder(
//                     Order(symbol = "AAPL", bid_price = 173.10, quantity = 1)),
//                   10 seconds)
//    println(res)
//    val ip = Await.result(robinhood.getInvestmentProfile(), 10 seconds)
//    println(ip)
//    assert(robinhood.isUserLoggedIn(), true)
//    assert(!robinhood.getToken().isEmpty, true)
  }

}
