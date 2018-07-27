package com.panaseer.petl.pipeline.module

import scala.reflect.runtime.universe


object Documentation {

  def apply(name: String, description: String, optionType: universe.Type): Documentation = {

    val args = optionType.members.filterNot(_.isMethod).map(symbol => {
      val field = symbol.name.toString
      val fieldType = symbol.typeSignature.toString
      ArgumentDescription(field, fieldType, "", Seq())
    }).toSeq

    Documentation(name, description, args)
  }

}

case class Documentation(stageName: String, stageDescription: String, arguments: Seq[ArgumentDescription])

case class ArgumentDescription(fieldName: String, fieldType: String, fieldDescription: String, examples: Seq[String])

