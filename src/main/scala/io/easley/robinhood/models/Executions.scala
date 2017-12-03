package io.easley.robinhood.models

import play.api.libs.json.{Format, Json}

object Executions {
  implicit val authFormat: Format[Executions] =
    Json.format[Executions]
}

case class Executions(timestamp: String,
                      price: String,
                      settlement_date: String,
                      id: String,
                      quantity: String) {}
