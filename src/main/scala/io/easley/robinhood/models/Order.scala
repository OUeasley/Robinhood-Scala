package io.easley.robinhood.models

import io.easley.robinhood.{Time, Trigger}
import play.api.libs.json.{Format, Json}

object Order {
  implicit val authFormat: Format[Order] =
    Json.format[Order]
}

case class Order(`type`: String = "limit",
                 symbol: String,
                 side: String = "",
                 account: String = "",
                 stop_price: Double = Double.NaN,
                 quantity: Int,
                 bid_price: Double,
                 instrument: String = "",
                 trigger: String = Trigger.GOOD_FOR_DAY.toString,
                 time: String = Time.IMMEDIATE.toString) {}