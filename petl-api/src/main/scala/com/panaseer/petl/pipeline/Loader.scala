package com.panaseer.petl.pipeline

trait Loader {

  def load(df: DataFrameLike): Unit

}
