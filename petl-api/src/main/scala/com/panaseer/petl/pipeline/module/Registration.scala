package com.panaseer.petl.pipeline.module

import java.util.ServiceLoader

import com.panaseer.petl.pipeline.arguments.Arguments
import com.panaseer.petl.pipeline.arguments.Arguments.Argument
import com.panaseer.petl.pipeline.{Extractor, Loader, PetlContext, Transformer}

import scala.reflect.runtime.universe

class Registration(private val map: Map[String, RegisteredComponent[_ <: Product]] = Map.empty) {

  def +(component: RegisteredComponent[_ <: Product]): Registration = {
    new Registration(map + (component.name -> component))
  }

  def get(name: String): Option[RegisteredComponent[_ <: Product]] = map.get(name)

  def getAll: Iterable[RegisteredComponent[_]] = map.values

  def ++(that: Registration): Registration = new Registration(this.map ++ that.map)

}

object Registration {

  val Empty = new Registration()

  def load: PetlContext => Registration = context => {
    import scala.collection.JavaConverters._

    ServiceLoader.load(classOf[PetlModule]).iterator().asScala
      .map(_.register(context))
      .foldLeft(Empty)(_ ++ _)
  }

  def apply(comp: RegisteredComponent[_ <: Product], comps: RegisteredComponent[_ <: Product]*): Registration = {
    val single: Map[String, RegisteredComponent[_ <: Product]] = Map(comp.name -> comp)
    val multiple: Map[String, RegisteredComponent[_ <: Product]] = comps.map(c => c.name -> c).toMap

    new Registration(single ++ multiple)
  }
}

sealed trait RegisteredComponent[A <: Product] {

  type Component

  def name: String

  def documentation: Documentation

  def argType: universe.Type

  def optionsFactory: Arguments => Argument[A]

  def componentFactory: A => Component

  def isExtractorComponent: Boolean = false

  def isLoaderComponent: Boolean = false

  def isTransformerComponent: Boolean = false

  def asExtractorComponent: ExtractorComponent[A] = throw ComponentError("Component is not an Extractor Component")

  def asLoaderComponent: LoaderComponent[A] = throw ComponentError("Component is not a Loader Component")

  def asTransformerComponent: TransformerComponent[A] = throw ComponentError("Component is not a Loader Component")
}

final case class ExtractorComponent[A <: Product](name: String,
                                                  documentation: Documentation,
                                                  argType: universe.Type,
                                                  optionsFactory: Arguments => Argument[A],
                                                  componentFactory: A => Extractor
                                                 ) extends RegisteredComponent[A] {
  override type Component = Extractor

  override def isExtractorComponent: Boolean = true

  override def asExtractorComponent: ExtractorComponent[A] = this
}

final case class LoaderComponent[A <: Product](name: String,
                                               documentation: Documentation,
                                               argType: universe.Type,
                                               optionsFactory: Arguments => Argument[A],
                                               componentFactory: A => Loader) extends RegisteredComponent[A] {

  override type Component = Loader

  override def isLoaderComponent: Boolean = true

  override def asLoaderComponent: LoaderComponent[A] = this
}

final case class TransformerComponent[A <: Product](name: String,
                                                    documentation: Documentation,
                                                    argType: universe.Type,
                                                    optionsFactory: Arguments => Argument[A],
                                                    componentFactory: A => Transformer) extends RegisteredComponent[A] {


  override type Component = Transformer

  override def isTransformerComponent: Boolean = true

  override def asTransformerComponent: TransformerComponent[A] = this
}

case class ComponentError(message: String) extends RuntimeException
