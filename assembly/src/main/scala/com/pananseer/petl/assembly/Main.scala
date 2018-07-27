package com.pananseer.petl.assembly

import com.panaseer.petl.pipeline.PetlContext
import com.panaseer.petl.pipeline.module.Registration

object Main {

  def main(args: Array[String]): Unit = {

    Registration.load(new PetlContext).getAll.foreach(println)
  }

}
