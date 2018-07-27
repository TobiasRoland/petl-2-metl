package com.panaseer.petl.hive

import com.panaseer.petl.pipeline.{DataFrameLike, Loader}

case class HiveLoader(args: HiveLoaderArgs) extends Loader {

  override def load(df: DataFrameLike): Unit = {
    println("executing")
  }
}

case class HiveLoaderArgs(tableName: String, partitions: Seq[String])

