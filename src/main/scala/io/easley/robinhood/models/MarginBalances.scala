package io.easley.robinhood.models

import play.api.libs.json.{Format, Json}

object MarginBalances {
  implicit val authFormat: Format[MarginBalances] =
    Json.format[MarginBalances]
}

case class MarginBalances(day_trade_buying_power: String,
                          start_of_day_overnight_buying_power: String,
                          overnight_buying_power_held_for_orders: String,
                          cash_held_for_orders: String,
                          created_at: String,
                          unsettled_debit: String,
                          start_of_day_dtbp: String,
                          day_trade_buying_power_held_for_orders: String,
                          overnight_buying_power: String,
                          marked_pattern_day_trader_date: String,
                          cash: String,
                          unallocated_margin_cash: String,
                          updated_at: String,
                          cash_available_for_withdrawal: String,
                          margin_limit: String,
                          outstanding_interest: String,
                          uncleared_deposits: String,
                          unsettled_funds: String,
                          gold_equity_requirement: String,
                          day_trade_ratio: String,
                          overnight_ratio: String
                         ) {}
