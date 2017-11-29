package io.easley.robinhood.models

import play.api.libs.json.{Format, Json}

object AuthToken {
  implicit val authFormat: Format[AuthToken] = Json.format[AuthToken]
}

case class AuthToken(token: String) {}