package io.easley.robinhood.models

import play.api.libs.json.{Format, Json}

object InstantEligibility {
  implicit val authFormat: Format[InstantEligibility] = Json.format[InstantEligibility]
}

case class InstantEligibility(updated_at: Option[String],
                              reason: String,
                              reinstatement_date: Option[String],
                              reversal: Option[String],
                              state: String) {}
