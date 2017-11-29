package io.easley.robinhood.models

import play.api.libs.json.{Format, Json}

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