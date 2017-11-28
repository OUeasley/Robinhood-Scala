package io.easley.robinhood

import play.api.libs.json._

object EnumUtils {
  def enumReads[E <: Enumeration](enum: E): Reads[E#Value] =
    new Reads[E#Value] {
      def reads(json: JsValue): JsResult[E#Value] = json match {
        case JsString(s) => {
          try {
            JsSuccess(enum.withName(s))
          } catch {
            case _: NoSuchElementException =>
              JsError(
                s"Enumeration expected of type: '${enum.getClass}', but it does not appear to contain the value: '$s'")
          }
        }
        case _ => JsError("String value expected")
      }
    }

  implicit def enumWrites[E <: Enumeration]: Writes[E#Value] =
    new Writes[E#Value] {
      def writes(v: E#Value): JsValue = JsString(v.toString)
    }

  implicit def enumFormat[E <: Enumeration](enum: E): Format[E#Value] = {
    Format(EnumUtils.enumReads(enum), EnumUtils.enumWrites)
  }
}

// Now let's test it

object EnumTest {

  object EnumA extends Enumeration {
    type EnumA = Value
    val VAL1, VAL2, VAL3 = Value
  }

  implicit val enumAFormat = EnumUtils.enumFormat(EnumA)
  val myEnumJson: JsValue = Json.toJson(EnumA.VAL1)
  val myValue: EnumA.Value = myEnumJson
    .asOpt[EnumA.Value]
    .getOrElse(sys.error("Oh noes! Invalid value!"))

  def ok = EnumA.VAL1 == myValue
}
