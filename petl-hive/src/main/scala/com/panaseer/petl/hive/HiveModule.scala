package com.panaseer.petl.hive

import cats.implicits._
import com.panaseer.petl.pipeline.PetlContext
import com.panaseer.petl.pipeline.module.{loader, _}

class HiveModule extends PetlModule {

  override def register(petlContext: PetlContext): Registration = {

    val hiveLoader = loader[HiveLoaderArgs]("hiveLoader")
      .loader(HiveLoader.apply)
      .description(
        "Loads data into Hive using some args"
      )
      .options(arguments => {
        (arguments.get[String]("tableName"),
          arguments.get[List[String]]("partitions")
        ).mapN(HiveLoaderArgs.apply)
      })

    val hiveExtractor = extractor[HiveExtractorOptions]("hiveExtractor")
      .extractor(HiveExtractor.apply)
      .description("Extracts data from Hive")
      .options(arguments => {
        (arguments.get[String]("tableName"),
          arguments.get[List[String]]("partitions")
        ).mapN(HiveExtractorOptions.apply)
      })

    Registration(hiveLoader, hiveExtractor)
  }

}
