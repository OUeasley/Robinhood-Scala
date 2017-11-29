package io.easley.robinhood.models

import play.api.libs.functional.syntax.unlift
import play.api.libs.json._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

object Instrument {
  implicit val authFormat: Format[Instrument] =
    Json.format[Instrument]
}

case class Instrument(min_tick_size: Option[String],
                      `type`: String,
                      splits: String,
                      margin_initial_ratio: String,
                      url: String,
                      quote: String,
                      tradability: String,
                      bloomberg_unique: String,
                      list_date: String,
                      name: String,
                      symbol: String,
                      fundamentals: String,
                      state: String,
                      country: String,
                      day_trade_ratio: String,
                      tradeable: Boolean,
                      maintenance_ratio: String,
                      id: String,
                      market: String,
                      simple_name: String)

object InstrumentArray {
  //  implicit val authFormat: Format[InstrumentArray] =
  //    Json.format[InstrumentArray]


  implicit val instrumentArrayReads: Reads[InstrumentArray] = (
    (JsPath \ "previous").readNullable[String] and
      (JsPath \ "results").read[List[Instrument]] and
      (JsPath \ "next").readNullable[String]
    )(InstrumentArray.apply _)

  implicit val instrumentArrayWrites: Writes[InstrumentArray] = (
    (JsPath \ "previous").writeNullable[String] and
      (JsPath \ "results").write[List[Instrument]] and
      (JsPath \ "next").writeNullable[String]
    )(unlift(InstrumentArray.unapply))
}



case class InstrumentArray(previous: Option[String],
                           results: List[Instrument],
                           next: Option[String])