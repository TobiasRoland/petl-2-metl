package com.panaseer.petl.pipeline

import io.circe.Json

sealed trait PetlArg {}

case class PetlInt(x: Int) extends PetlArg

case class PetlDouble(x: Double) extends PetlArg

case class PetlString(x: String) extends PetlArg

case class PetlBool(x: Boolean) extends PetlArg

case class PetlArray(x: Seq[PetlArg]) extends PetlArg

case class PetlObject(x: Map[String, PetlArg]) extends PetlArg

case object PetlNone extends PetlArg

trait PetlArgParser[A] {
  def parse(a: A): PetlArg // Should get wrapped in a Result-type to handle parsing errors
}

class JsonToArgs extends PetlArgParser[Json] {

  type AsPetlArg = Json => Option[PetlArg]
  private[this] val asObject: AsPetlArg = json => json.asObject.map(_ => parse(json))
  private[this] val asArray: AsPetlArg = json => json.asArray.map(_.map(parse)).map(PetlArray)
  private[this] val asBool: AsPetlArg = _.asBoolean.map(PetlBool)
  private[this] val asNone: AsPetlArg = _.asNull.map(_ => PetlNone)
  private[this] val asInt: AsPetlArg = _.asNumber.flatMap(_.toInt).map(PetlInt)
  private[this] val asDouble: AsPetlArg = _.asNumber.map(_.toDouble).map(PetlDouble)
  private[this] val asString: AsPetlArg = _.asString.map(PetlString)

  override def parse(json: Json): PetlArg = {
    val raw: Map[String, Json] = json.asObject.map(_.toMap).get
    val resolved: Map[String, PetlArg] = raw.mapValues(parseVal)
    PetlObject(resolved)
  }

  private[this] def parseVal(j: Json): PetlArg = {
    val parsed = asObject(j)
      .orElse(asArray(j))
      .orElse(asBool(j))
      .orElse(asNone(j))
      .orElse(asInt(j))
      .orElse(asDouble(j))
      .orElse(asString(j))

    parsed match {
      case None => throw new IllegalStateException(s"Unknown JSON type: $j")
      case Some(petlArg) => petlArg
    }
  }

}
