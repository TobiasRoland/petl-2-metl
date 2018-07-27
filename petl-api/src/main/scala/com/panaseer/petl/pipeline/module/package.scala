
package com.panaseer.petl.pipeline

import com.panaseer.petl.pipeline.arguments.Arguments
import com.panaseer.petl.pipeline.arguments.Arguments.Argument

import scala.language.implicitConversions
import scala.reflect.runtime.universe._

package object module {

  implicit def build[A <: Product, C <: RegisteredComponent[A]](builder: ComponentBuilder[A, C]): C = {
    builder.build
  }

  def extractor[A <: Product : TypeTag](name: String): ExtractorComponentBuilder[A] = {
    val argType = implicitly[TypeTag[A]]
    new ExtractorComponentBuilder[A](name, argType.tpe)
  }

  def transformer[A <: Product : TypeTag](name: String): TransformerComponentBuilder[A] = {
    val argType = implicitly[TypeTag[A]]
    new TransformerComponentBuilder[A](name, argType.tpe)
  }

  def loader[A <: Product : TypeTag](name: String): LoaderComponentBuilder[A] = {
    val argType = implicitly[TypeTag[A]]
    new LoaderComponentBuilder[A](name, argType.tpe)
  }

  sealed abstract class ComponentBuilder[A <: Product, C <: RegisteredComponent[A]](name: String, argType: Type) {

    protected var documentation: Documentation = _
    protected var optionsFactory: Arguments => Argument[A] = _

    final def description(desc: String): ComponentBuilder[A, C] = {
      this.documentation = Documentation(name, desc, argType)
      this
    }

    final def options(opt: Arguments => Argument[A]): ComponentBuilder[A, C] = {
      this.optionsFactory = opt
      this
    }

    def build: C
  }

  class ExtractorComponentBuilder[A <: Product] private[module](name: String, argType: Type)
    extends ComponentBuilder[A, ExtractorComponent[A]](name, argType) {

    private[this] var componentFactory: A => Extractor = _

    def extractor(sup: A => Extractor): ExtractorComponentBuilder[A] = {
      this.componentFactory = sup
      this
    }

    def build: ExtractorComponent[A] = ExtractorComponent(name, documentation, argType, optionsFactory, componentFactory)
  }

  class TransformerComponentBuilder[A <: Product] private[module](name: String, argType: Type)
    extends ComponentBuilder[A, TransformerComponent[A]](name, argType) {

    private[this] var componentFactory: A => Transformer = _

    def transformer(sup: A => Transformer): TransformerComponentBuilder[A] = {
      this.componentFactory = sup
      this
    }

    def build: TransformerComponent[A] = TransformerComponent(name, documentation, argType, optionsFactory, componentFactory)

  }

  class LoaderComponentBuilder[A <: Product] private[module](name: String, argType: Type)
    extends ComponentBuilder[A, LoaderComponent[A]](name, argType) {

    private[this] var componentFactory: A => Loader = _

    def loader(sup: A => Loader): LoaderComponentBuilder[A] = {
      this.componentFactory = sup
      this
    }

    def build: LoaderComponent[A] = LoaderComponent(name, documentation, argType, optionsFactory, componentFactory)

  }

}
