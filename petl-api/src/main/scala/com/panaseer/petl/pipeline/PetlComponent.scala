package com.panaseer.petl.pipeline

trait Stage {

  def `type`: String

  def args: Map[String, String]
}

case class Pipeline(name: String, vars: Map[String, String], stages: Seq[Stage])

case class StageImpl(name: String, `type`: String, args: Map[String, String]) extends Stage
