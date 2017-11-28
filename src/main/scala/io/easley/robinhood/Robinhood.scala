package io.easley.robinhood

import java.lang.Throwable

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.Logger
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import io.easley.robinhood.Time.Time
import io.easley.robinhood.Trigger.Trigger
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import ai.x.play.json.Jsonx

import scala.util.control.Exception

class Robinhood {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val logger = Logger(Robinhood.getClass)

  import Robinhood._

  private def getAccounts() = {
    makeGetRequest(uri = Endpoints.ACCOUNTS)
      .flatMap(resp => Unmarshal(resp).to[AccountArray])
      .map(aa => {
        val account = aa.results.head
        acct = account
        account
      })
  }

  def getAccount() = {
    acct
  }

  def getInvestmentProfile() = {
    makeGetRequest(uri = Endpoints.INVESTMENT_PROFILE)
      .flatMap(resp => Unmarshal(resp).to[InvestmentProfile])
  }

  def getInstrument(symbol: String) = {
    makeGetRequest(uri = s"${Endpoints.INSTRUMENTS}$symbol/")
      .flatMap(resp => Unmarshal(resp).to[InstrumentArray])
      .map(ia => ia.results.head)
  }

  def getOrder(orderId: String) = {
    makeGetRequest(uri = s"${Endpoints.ORDERS}$orderId").flatMap(resp =>
      Unmarshal(resp).to[SecurityOrderElement])
  }

  def placeBuyOrder(order: Order) = {
    getInstrument(order.symbol)
      .map(instrument => {
        order.copy(instrument = instrument.url,
                   account = acct.url,
                   side = "buy")
      })
      .flatMap(order => Marshal(order).to[RequestEntity])
      .flatMap(requestEntity => {
        makePostRequest(entity = requestEntity, uri = Endpoints.ORDERS)
      })
  }

  def login(username: String, password: String) = {
    if (authToken.isEmpty) {
      val obj = Json.obj("username" -> username, "password" -> password)
      val token = Marshal(obj)
        .to[RequestEntity]
        .map(
          x =>
            HttpRequest(headers = getHeaders(),
                        entity = x,
                        method = HttpMethods.POST,
                        uri = Endpoints.BASE_URL + Endpoints.LOGIN))
        .flatMap(request => makeRequest(request))
        .flatMap(x => {
          Unmarshal(x)
            .to[AuthToken]
            .map(token => {
              authToken = token.token
              isLoggedIn = true
              getAccounts()
              logger.info("User is logged in.")
              token
            })
        })
      token.recover {
        case t: Throwable =>
          logger.error("User could not be logged in. ")
          logger.error(t.getMessage)
      }
      token
    } else {
      Future.successful(AuthToken(authToken))
    }
  }

  private def makeRequest(request: HttpRequest) = {
    Http().singleRequest(request)
  }

  def getHeaders() = {
    if (isLoggedIn) {
      headers.:+(RawHeader("Authorization", "Token " + authToken))
    } else {
      headers
    }
  }

  def getToken() = {
    authToken
  }

  def getQuote(symbol: String) = {
    makeGetRequest(uri = Endpoints.QUOTES + symbol)
      .flatMap(resp => Unmarshal(resp).to[Quote])
  }

  def isUserLoggedIn() = {
    isLoggedIn
  }

  def logout() = {
    logger.info("User logged out.")
    makePostRequest(uri = Endpoints.LOGOUT)
    isLoggedIn = false
    authToken = ""
  }

  private def makePostRequest(entity: RequestEntity = HttpEntity.Empty,
                              uri: String = "") = {
    makeRequest(
      HttpRequest(method = HttpMethods.POST,
                  headers = getHeaders(),
                  entity = entity,
                  uri = Endpoints.BASE_URL + uri))
  }

  private def makeGetRequest(entity: RequestEntity = HttpEntity.Empty,
                             uri: String = "") = {
    makeRequest(
      HttpRequest(method = HttpMethods.GET,
                  headers = getHeaders(),
                  entity = entity,
                  uri = Endpoints.BASE_URL + uri))
  }

}

object Robinhood {

  var headers: scala.collection.immutable.Seq[HttpHeader] =
    scala.collection.immutable.Seq(
      RawHeader("Accept", "*/*"),
      RawHeader("Accept-Encoding", "gzip, deflate"),
      RawHeader("Accept-Language",
                "en;q=1, fr;q=0.9, de;q=0.8, ja;q=0.7, nl;q=0.6, it;q=0.5"),
      RawHeader("Content-Type",
                "application/x-www-form-urlencoded; charset=utf-8"),
      RawHeader("Connection", "keep-alive"),
      RawHeader("X-Robinhood-API-Version", "1.152.0"),
      RawHeader(
        "User-Agent",
        "Robinhood/5.32.0 (com.robinhood.release.Robinhood; build:3814; iOS 10.3.3)")
    )
  var isLoggedIn = false
  var authToken = ""
  var acct: Account = _

}

object TriggerEnum {
  implicit val triggerEnumFormat = new Format[Trigger] {
    def reads(json: JsValue) = JsSuccess(Trigger.withName(json.as[String]))
    def writes(myEnum: Trigger) = JsString(myEnum.toString)
  }
}

object TimeEnum {
  implicit val timeEnumFormat = new Format[Time] {
    def reads(json: JsValue) = JsSuccess(Time.withName(json.as[String]))
    def writes(myEnum: Time) = JsString(myEnum.toString)
  }
}

object Trigger extends Enumeration {
  type Trigger = Value
  val GOOD_FOR_DAY = Value("gfd")
  val GOOD_TILL_CANCELLED = Value("gtc")
  val ORDER_CANCELS_OTHER = Value("oco")
}

object Time extends Enumeration {
  type Time = Value
  val IMMEDIATE = Value("immediate")
  val DAY = Value("day")
}

object Account {
  implicit val authFormat: Format[Account] =
    Jsonx.formatCaseClass[Account]
}

case class Account(deactivated: Boolean,
                   updated_at: String,
                   margin_balances: JsObject,
                   portfolio: String,
                   cash_balances: String,
                   withdrawal_halted: Boolean,
                   cash_available_for_withdrawal: Double,
                   `type`: String,
                   sma: Double,
                   sweep_enabled: Boolean,
                   deposit_halted: Boolean,
                   buying_power: Double,
                   user: String,
                   max_ach_early_access_amount: String,
                   cash_held_for_orders: String,
                   only_position_closing_trades: Boolean,
                   url: String,
                   positions: String,
                   created_at: String,
                   cash: String,
                   sma_held_for_orders: String,
                   account_number: String,
                   uncleared_deposits: String,
                   unsettled_funds: String)

object AccountArray {
  implicit val authFormat: Format[AccountArray] =
    Json.format[AccountArray]
}

case class AccountArray(previous: String, results: Seq[Account], next: String)

object AuthToken {
  implicit val authFormat: Format[AuthToken] = Json.format[AuthToken]
}

case class AuthToken(token: String) {}

object Instrument {
  implicit val authFormat: Format[Instrument] =
    Json.format[Instrument]
}

case class Instrument(min_tick_size: String,
                      splits: String,
                      margin_initial_ratio: String,
                      url: String,
                      quote: String,
                      symbol: String,
                      bloomberg_unique: String,
                      list_date: String,
                      fundamentals: String,
                      state: String,
                      day_trade_ratio: String,
                      tradeable: Boolean,
                      maintenance_ratio: String,
                      id: String,
                      market: String,
                      name: String)

object InstrumentArray {
  implicit val authFormat: Format[InstrumentArray] =
    Json.format[InstrumentArray]
}

case class InstrumentArray(previous: String,
                           results: Seq[Instrument],
                           next: String)

object InvestmentProfile {
  implicit val authFormat: Format[InvestmentProfile] =
    Json.format[InvestmentProfile]
}

case class InvestmentProfile(annual_income: String,
                             investment_experience: String,
                             updated_at: String,
                             risk_tolerance: String,
                             total_net_worth: String,
                             liquidity_needs: String,
                             investment_objective: String,
                             source_of_funds: String,
                             user: String,
                             suitability_verified: Boolean,
                             tax_bracket: String,
                             time_horizon: String,
                             liquid_net_worth: String)

object Order {
  implicit val authFormat: Format[Order] =
    Json.format[Order]
}

case class Order(`type`: String = "limit",
                 symbol: String,
                 side: String,
                 account: String,
                 stop_price: Double = Double.NaN,
                 quantity: Int,
                 bid_price: Double,
                 instrument: String,
                 trigger: String = Trigger.GOOD_FOR_DAY.toString,
                 time: String = Time.IMMEDIATE.toString) {}

object SecurityOrderElement {
  implicit val authFormat: Format[SecurityOrderElement] =
    Json.format[SecurityOrderElement]
}

case class SecurityOrderElement(executions: Seq[String],
                                fees: Float,
                                cancel: String,
                                id: String,
                                cumulative_quantity: String,
                                reject_reason: String,
                                state: String,
                                client_id: String,
                                url: String,
                                position: String,
                                average_price: Float,
                                extended_hours: Boolean,
                                override_day_trade_checks: Boolean,
                                override_dtbp_checks: Boolean)

object Quote {
  implicit val authFormat: Format[Quote] =
    Json.format[Quote]
}

case class Quote(
    ask_price: Float,
    ask_size: Int, // Integer
    bid_price: Float, // Float number in a String, e.g. '731.5000'
    bid_size: Int, // Integer
    last_trade_price: String, // Float number in a String, e.g. '726.3900'
    last_extended_hours_trade_price: String, // Float number in a String, e.g. '735.7500'
    previous_close: String, // Float number in a String, e.g. '743.6200'
    adjusted_previous_close: String, // Float number in a String, e.g. '743.6200'
    previous_close_date: String, // YYYY-MM-DD e.g. '2016-01-06'
    symbol: String, // e.g. 'AAPL'
    trading_halted: Boolean,
    updated_at: String)
