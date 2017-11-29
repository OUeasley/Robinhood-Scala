package io.easley.robinhood.models

import play.api.libs.json.{Format, Json}

object InvestmentProfile {
  implicit val authFormat: Format[InvestmentProfile] =
    Json.format[InvestmentProfile]
}

case class InvestmentProfile(annual_income: String,
                             investment_experience: String,
                             updated_at: String,
                             risk_tolerance: String,
                             total_net_worth: String,
                             liquidity_needs: String,
                             investment_objective: String,
                             source_of_funds: String,
                             user: String,
                             suitability_verified: Boolean,
                             tax_bracket: String,
                             time_horizon: String,
                             liquid_net_worth: String)
