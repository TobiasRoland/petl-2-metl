package com.panaseer.petl.hive

import com.panaseer.petl.pipeline.module.documented
import com.panaseer.petl.pipeline.{DataFrameLike, Extractor}

case class HiveExtractor(options: HiveExtractorOptions) extends Extractor {

  override def extract: DataFrameLike = {
    ???
  }
}

case class HiveExtractorOptions(@documented("", "") tableName: String,
                                @documented("", "") partitions: List[String])
