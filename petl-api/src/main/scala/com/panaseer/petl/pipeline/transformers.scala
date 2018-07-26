package com.panaseer.petl.pipeline

sealed trait Transformer

trait Transformer1 extends Transformer {

  def transformer(df: DataFrameLike): DataFrameLike

}

trait Transformer2 extends Transformer {

  def transformer(df1: DataFrameLike, df2: DataFrameLike): DataFrameLike
}


