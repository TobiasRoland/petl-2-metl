package com.panaseer.petl.pipeline

import cats.implicits._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveDecoder
import io.circe.{DecodingFailure, Json, JsonObject}

import scala.collection.immutable
import scala.util.matching.Regex

trait Decoder {

  def parse(toParse: String): Either[String, Pipeline]

}

class DecoderImpl(registry: PetlRegistry) extends Decoder {

  implicit val configuration: Configuration = Configuration.default.withDefaults

  import io.circe.generic.extras.semiauto.{deriveDecoder, deriveEncoder}

  implicit val decodeStage: io.circe.Decoder[StageImpl] = deriveDecoder


  override def parse(toParse: String): Either[String, Pipeline] = {
    import io.circe.yaml.parser

    def getName(json: Json): Either[String, String] = {
      json.hcursor.downField("name").as[String].left.map(_.getMessage())
    }

    def vars(json: Json): Either[String, Map[String, String]] = (json \\ "vars")
      .flatMap(_.asObject)
      .map(_.toMap)
      .map(_.mapValues(_.asString.get))
      .reduce(_ ++ _).asRight

    def interpolateStages(vars: Map[String, String])(json: Json) = json \\ "stages" match {
      case x :: Nil => transform(x, interpolate(vars)).asArray.getOrElse(Seq()).asRight
      case _ => "Stage is weird".asLeft
    }

    def stages(js: Seq[Json]): Either[String, Seq[Stage]] = {
      js.map(json => {

        decodeStage.decodeJson(json)  match {
          case Left(e) =>
            println(e)
            e.toString()
            throw e
          case Right(good) => good
        }

      }).asRight
    }

    def parse = {
      parser.parse(toParse).left.map(_.message)
    }

    for {
      parsed <- parse
      name <- getName(parsed)
      vars <- vars(parsed)
      interpolated <- interpolateStages(vars)(parsed)
      s <- stages(interpolated)
    } yield Pipeline(name, vars, s)

  }

  private[this] val shouldBeInterpolated: Regex = """\$\{(.+)}""".r

  def interpolate(keys: Map[String, String])(target: String): String = target match {
    case shouldBeInterpolated(value) => keys.getOrElse(value, "INVALID DERP")
    case _ => target
  }

  def transformObjectKeys(obj: JsonObject, f: String => String): JsonObject =
    JsonObject.fromIterable(
      obj.toList.map {
        case (k, v) if v.isString => k -> Json.fromString(f(v.asString.get))
        case (k, v) => k -> transform(v, f)
      }
    )

  def transform(json: Json, f: String => String): Json = json.arrayOrObject(
    json,
    array => Json.fromValues(array.map(transform(_, f))),
    obj => Json.fromJsonObject(transformObjectKeys(obj, f)))
}
