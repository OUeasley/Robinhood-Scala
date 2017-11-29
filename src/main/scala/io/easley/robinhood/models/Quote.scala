package io.easley.robinhood.models

import play.api.libs.json.{Format, Json}

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
