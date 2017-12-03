package io.easley.robinhood.models

import io.easley.robinhood.{Time, Trigger}
import play.api.libs.json.{Format, Json}

object Order {
  implicit val authFormat: Format[Order] =
    Json.format[Order]
}

case class Order(
                  account: String = "",
                  instrument: String = "",
                  price: Double,
                  quantity: Int,
                  side: String = "",
                  symbol: String,
                  time_in_force: String = Trigger.GOOD_FOR_DAY.toString,
                  trigger: String = Time.IMMEDIATE.toString,
                  `type`: String = "limit",
                 stop_price: Option[Double] = None
                 ) {}
