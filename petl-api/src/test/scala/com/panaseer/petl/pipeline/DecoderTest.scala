package com.panaseer.petl.pipeline

import org.scalatest.{FlatSpec, Matchers}

class DecoderTest extends FlatSpec with Matchers {

  "Decoder" should "decode" in {

    val pipeline =
      """
        |name: Ingestion of Qualys
        |vars:
        |  inputTableName: 'r_qualys'
        |  outputTableName: 'r_qualys_renamed'
        |stages:
        |- name: common-ingest
        |  type: pipeline
        |  args:
        |    inputTableName: '${inputTableName}'
        |    outputTableName: '${outputTableName}'
        |""".stripMargin

    val expected = Pipeline(
      name = "Ingestion of Qualys",
      vars = Map("inputTableName" -> "r_qualys", "outputTableName" -> "r_qualys_renamed"),
      stages = Seq(PetlObject(Map(
        "name" -> PetlString("common-ingest"),
        "type" -> PetlString("pipeline"),
        "args" -> PetlObject(Map(
          "inputTableName" -> PetlString("r_qualys"),
          "outputTableName" -> PetlString("r_qualys_renamed")
        ))
      ))
      )
    )

    new DecoderImpl().parse(pipeline) shouldBe Right(expected)
  }


}
