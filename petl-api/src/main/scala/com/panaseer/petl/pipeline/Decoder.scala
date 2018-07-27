package com.panaseer.petl.pipeline

import cats.implicits._
import io.circe
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveDecoder
import io.circe.{Json, JsonObject, yaml}

import scala.util.Try
import scala.util.matching.Regex

trait Decoder {
  def parse(toParse: String): Either[String, Pipeline]
}

class DecoderImpl(decodePetlArgs: JsonToArgs = new JsonToArgs()) extends Decoder {

  case class NameAndVars(name: String, vars: Map[String, String] = Map())
  case class NameAndType(name: String, `type`: String)

  implicit val configuration: Configuration = Configuration.default.withDefaults
  implicit val decodeNameAndVars: circe.Decoder[NameAndVars] = deriveDecoder
  implicit val decodeNameAndType: circe.Decoder[NameAndType] = deriveDecoder

  override def parse(toParse: String): Either[String, Pipeline] = {
    for {
      json <- yamlToJson(toParse)
      nameAndVars <- json.as[NameAndVars].left.map(_.getMessage())
      interpolated <- interpolatedStages(json, nameAndVars.vars)
      stages <- parseStages(interpolated)
    } yield Pipeline(nameAndVars.name, nameAndVars.vars, stages)
  }

  def parseStages(rawStages: Seq[Json]): Either[String, Seq[PetlArg]] = rawStages
    .map(stage => decodePetlArgs.parse(stage))
    .asRight

  def yamlToJson(toParse: String): Either[String, Json] = yaml.parser.parse(toParse).left.map(_.message)

  def interpolatedStages(json: Json, vars: Map[String, String]): Either[String, Seq[Json]] = json \\ "stages" match {
    case  Nil => s"No [stages]` key defined in $json".asLeft
    case stages :: Nil => {
      val interpolatedJson = transformStringValues(interpolate(vars), stages)
      interpolatedJson.asArray.getOrElse(Seq()).asRight
    }
    case otherwise => s"Expected only one key of [stages], found ${otherwise.length} in $json".asLeft
  }

  private[this] val shouldBeInterpolated: Regex = """\$\{(.+)}""".r

  def interpolate(keys: Map[String, String])(target: String): String = target match {
    case shouldBeInterpolated(value) => keys.getOrElse(value, "INVALID DERP")
    case _ => target
  }

  def transformStringValues(f: String => String, json: Json): Json = json
    .mapString(f)
    .mapArray(a => a.map(transformStringValues(f, _)))
    .mapObject(obj => JsonObject(obj.toMap.mapValues(transformStringValues(f, _)).toSeq: _*))

}
