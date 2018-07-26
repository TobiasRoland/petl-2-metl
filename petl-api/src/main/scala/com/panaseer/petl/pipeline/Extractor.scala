package com.panaseer.petl.pipeline

trait Extractor {

  def extract: DataFrameLike

}
