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
import scala.concurrent.{Await, Future}
import ai.x.play.json.Jsonx
import akka.http.scaladsl.server.{RejectionError, ValidationRejection}
import io.easley.robinhood.models._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import scala.concurrent.duration._
import scala.util.control.Exception

class Robinhood {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val logger = Logger(Robinhood.getClass)

  import Robinhood._

   def getAccounts() = {
    makeGetRequest(uri = Endpoints.ACCOUNTS)
      .flatMap(resp => {
        Unmarshal(resp).to[AccountArray]

      })
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
    makeGetRequest(uri = s"${Endpoints.INSTRUMENTS}?query=$symbol")
      .flatMap(resp => {
        Unmarshal(resp).to[InstrumentArray]
      })
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
      .map(order => {
        val orderJson = Json.toJson(order)
        HttpEntity(ContentTypes.`application/json`,orderJson.toString())
      })
      .flatMap(requestEntity => {
        makePostRequest(entity = requestEntity, uri = Endpoints.ORDERS,headers = getHeadersJson())
      }).map(resp => {
      Unmarshal(resp).to[SecurityOrderElement]
    })
  }

  def placeSellOrder(order: Order) = {
    getInstrument(order.symbol)
      .map(instrument => {
        order.copy(instrument = instrument.url,
          account = acct.url,
          side = "sell")
      })
      .map(order => {
        val orderJson = Json.toJson(order)
        HttpEntity(ContentTypes.`application/json`,orderJson.toString())
      })
      .flatMap(requestEntity => {
        makePostRequest(entity = requestEntity, uri = Endpoints.ORDERS,headers = getHeadersJson())
      }).map(resp => {
      Unmarshal(resp).to[SecurityOrderElement]
    })
  }

  def cancelOrder(orderId : String) ={
    makePostRequest(uri = s"${Endpoints.ORDERS}$orderId/cancel/",headers = getHeadersJson()).flatMap(resp =>
      Unmarshal(resp).to[SecurityOrderElement])
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

  def getHeadersJson() = {
    if (isLoggedIn) {
      headersJson.:+(RawHeader("Authorization", "Token " + authToken))
    } else {
      headersJson
    }
  }

  def getHeaders() = {
    if (isLoggedIn) {
        headers.:+(RawHeader("Authorization", "Token " + authToken))
    } else {
      headers
    }
  }

  def setToken(str : String) = {
    authToken = str
  }

  def setUserLoggedIn(): Unit ={
    isLoggedIn = true
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
                              headers : Seq[HttpHeader] = getHeaders(),
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


  var headersJson: scala.collection.immutable.Seq[HttpHeader] =
    scala.collection.immutable.Seq(
      RawHeader("Accept", "*/*"),
      RawHeader("Accept-Encoding", "gzip, deflate"),
      RawHeader("Accept-Language",
        "en;q=1, fr;q=0.9, de;q=0.8, ja;q=0.7, nl;q=0.6, it;q=0.5"),
      RawHeader("Content-Type",
        "application/json"),
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