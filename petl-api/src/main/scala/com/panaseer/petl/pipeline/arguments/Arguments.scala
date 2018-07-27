package com.panaseer.petl.pipeline.arguments

import cats.data._
import cats.implicits._
import com.panaseer.petl.pipeline.arguments.Arguments.Argument

class Arguments(args: Map[String, Any]) {

  def get[A: ArgumentDecoder](key: String): Argument[A] = {
    val decoder = ArgumentDecoder[A]
    args.get(key).map(decoder.decode).getOrElse(ArgumentError(s"$key does not exist").invalidNel)
  }

  def getOrDefault[A: ArgumentDecoder](key: String)(sup: () => A): Argument[A] = {
    val decoder = ArgumentDecoder[A]
    args.get(key).map(decoder.decode).getOrElse(sup().validNel)
  }

}

object Arguments {

  type Argument[A] = ValidatedNel[ArgumentError, A]
}

case class ArgumentError(message: String) extends RuntimeException

trait ArgumentDecoder[A] {

  def decode(args: Any): Argument[A]

}

object ArgumentDecoder {

  def apply[A: ArgumentDecoder]: ArgumentDecoder[A] = implicitly[ArgumentDecoder[A]]

  def instance[A](f: PartialFunction[Any, Argument[A]]): ArgumentDecoder[A] = new ArgumentDecoder[A] {

    override def decode(args: Any): Argument[A] =
      if (f.isDefinedAt(args)) {
        f(args)
      } else {
        ArgumentError("Unable to translate types").invalidNel
      }
  }

  implicit val stringDecoder: ArgumentDecoder[String] = instance {
    case arg: String => arg.validNel
  }

  implicit val intDecoder: ArgumentDecoder[Int] = instance {
    case arg: Int => arg.validNel
  }

  implicit val booleanDecoder: ArgumentDecoder[Boolean] = instance {
    case arg: Boolean => arg.validNel
  }

  implicit def seqDecoder[A](implicit d: ArgumentDecoder[A]): ArgumentDecoder[List[A]] = instance {
    case arg: List[_] => arg.map(d.decode).sequence[Argument, A]
  }

}
