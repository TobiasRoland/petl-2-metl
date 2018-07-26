package com.panaseer.petl.pipeline

import com.panaseer.petl.pipeline.module.Registration

class PetlRegistry {

  def contains(name: String): Boolean = Seq("pipeline").contains(name)

  def register(toRegister: Registration): PetlRegistry = ???

  def getExtractor(name: String) = ???

  def getTransformer(name: String) = ???

  def getLoader(name: String) = ???

}
