package io.easley.robinhood.models

import ai.x.play.json.Jsonx
import play.api.libs.functional.syntax.unlift
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

object Account {
  implicit val authFormat: Format[Account] =
    Jsonx.formatCaseClass[Account]
}

case class Account(deactivated: Boolean,
                   updated_at: String,
                   margin_balances: MarginBalances,
                   portfolio: String,
                   cash_balances: Option[String],
                   can_downgrade_to_cash: String,
                   withdrawal_halted: Boolean,
                   cash_available_for_withdrawal: String,
                   `type`: String,
                   sma: String,
                   sweep_enabled: Boolean,
                   deposit_halted: Boolean,
                   buying_power: String,
                   user: String,
                   max_ach_early_access_amount: String,
                   instant_eligibility: InstantEligibility,
                   cash_held_for_orders: String,
                   only_position_closing_trades: Boolean,
                   url: String,
                   positions: String,
                   created_at: String,
                   cash: String,
                   sma_held_for_orders: String,
                   unsettled_debit: String,
                   account_number: String,
                   uncleared_deposits: String,
                   unsettled_funds: String)

object AccountArray {
  //  implicit val authFormat: Format[InstrumentArray] =
  //    Json.format[InstrumentArray]

  implicit val accountArrayReads: Reads[AccountArray] = (
    (JsPath \ "previous").readNullable[String] and
      (JsPath \ "results").read[List[Account]] and
      (JsPath \ "next").readNullable[String]
    )(AccountArray.apply _)

  implicit val accountArrayWrites: Writes[AccountArray] = (
    (JsPath \ "previous").writeNullable[String] and
      (JsPath \ "results").write[List[Account]] and
      (JsPath \ "next").writeNullable[String]
    )(unlift(AccountArray.unapply))
}

case class AccountArray(previous: Option[String], results: List[Account], next: Option[String])
