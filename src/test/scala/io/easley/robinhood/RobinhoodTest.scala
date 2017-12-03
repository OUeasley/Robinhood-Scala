package io.easley.robinhood

import io.easley.robinhood.models._
import org.scalatest.{FunSuite, Outcome}

import scala.concurrent.duration._
import org.scalatest._

import scala.concurrent.Await

class RobinhoodTest extends FunSuite {
  val robinhood = new Robinhood

  test("Robinhood should get user account token ") {

      robinhood.setUserLoggedIn()
      val res0 = Await.result(robinhood.getAccounts(), 10 seconds)
      println(res0)

      val res =
        Await.result(robinhood.cancelOrder(order),
          10 seconds)

      println(res)
      //    val ip = Await.result(robinhood.getInvestmentProfile(), 10 seconds)
      //    println(ip)/",
      //    assert(robinhood.isUserLoggedIn(), true)
      //    assert(!robinhood.getToken().isEmpty, true)
  }

}
