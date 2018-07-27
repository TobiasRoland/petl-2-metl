package com.panaseer.petl.pipeline

trait Stage {
  def args: PetlArg
}

case class Pipeline(name: String = "", vars: Map[String, String], stages: Seq[PetlArg])
