package com.panaseer.petl.pipeline.module

import com.panaseer.petl.pipeline.PetlContext

trait PetlModule {

  def register(petlContext: PetlContext): Registration

}
