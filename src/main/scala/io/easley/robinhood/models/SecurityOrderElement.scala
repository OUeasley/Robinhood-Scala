package io.easley.robinhood.models

import ai.x.play.json.Jsonx
import play.api.libs.json.{Format, Json}

object SecurityOrderElement {
  implicit val authFormat: Format[SecurityOrderElement] =
    Jsonx.formatCaseClass[SecurityOrderElement]
}

case class SecurityOrderElement(
                                 updated_at : String,
                                ref_id : Option[String],
                                time_in_force : String,
                                fees: String,
                                cancel : String,
                                id : String,
                                cumulative_quantity : String,
                                stop_price : Option[String],
                                reject_reason : Option[String],
                                instrument : String,
                                state : String,
                                trigger : String,
                                override_dtbp_checks : Boolean,
                               `type` : String,
                                last_transaction_at : String,
                                price : String,
                                executions: Option[Seq[Executions]],
                                extended_hours : Boolean,
                                account : String,
                                url : String,
                                created_at : String,
                                side: String,
                                override_day_trade_checks : Boolean,
                                position : String,
                                average_price : Option[String],
                                quantity : String
                               )