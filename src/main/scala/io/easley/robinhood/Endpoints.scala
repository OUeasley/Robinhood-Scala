package io.easley.robinhood

object Endpoints {
  val BASE_URL = "https://api.robinhood.com/"
  val LOGIN = "api-token-auth/"
  val LOGOUT = "api-token-logout/"
  val INVESTMENT_PROFILE = "user/investment_profile/"
  val ACCOUNTS = "accounts/"
  val ACH_IAV_AUTH = "ach/iav/auth/"
  val ACH_RELATIONSHIPS = "ach/relationships/"
  val ACH_TRANSFERS = "ach/transfers/"
  val ACH_DEPOSIT_SCHEDULES = "ach/deposit_schedules/"
  val APPLICATIONS = "applications/"
  val DIVIDENDS = "dividends/"
  val EDOCUMENTS = "documents/"
  val INSTRUMENTS = "instruments/"
  val MARGIN_UPGRADE = "margin/upgrades/"
  val MARKETS = "markets/"
  val NOTIFICATIONS = "notifications/"
  val NOTIFICATIONS_DEVICES = "notifications/devices/"
  val ORDERS = "orders/"
  val CANCEL_ORDER = "orders/" //API expects https=//api.robinhood.com/orders/{{orderId}}/cancel/
  val PASSWORD_RESET = "password_reset/request/"
  val QUOTES = "quotes/"
  val DOCUMENT_REQUESTS = "upload/document_requests/"
  val USER = "user/"

  val USER_ADDITIONAL_INFO = "user/additional_info/"
  val USER_BASIC_INFO = "user/basic_info/"
  val USER_EMPLOYMENT = "user/employment/"
  val USER_INVESTMENT_PROFILE = "user/investment_profile/"

  val WATCHLISTS = "watchlists/"
  val POSITIONS = "positions/"
  val FUNDAMENTALS = "fundamentals/"
  val SP500_UP = "midlands/movers/sp500/?direction=up"
  val SP500_DOWN = "midlands/movers/sp500/?direction=down"
  val NEWS = "midlands/news/"
}
