package com.panaseer.petl.pipeline

import org.scalatest.{FlatSpec, Matchers}

class DecoderTest extends FlatSpec with Matchers {

  "Decoder" should "decode" in {

    val pipeline ="""name: Ingestion of Qualys
vars:
  inputTableName: 'r_qualys'
  outputTableName: 'r_qualys_renamed'
stages:
- name: common-ingest
  type: pipeline
  args:
    inputTableName: '${inputTableName}'
    outputTableName: '${outputTableName}'"""

    val expected = Pipeline(
      "Ingestion of Qualys",
      Map("inputTableName" -> "r_qualys", "outputTableName" -> "r_qualys_renamed"),
      Seq(StageImpl("common-ingest", "pipeline", Map("inputTableName" -> "r_qualys", "outputTableName" -> "r_qualys_renamed")))
    )

    new DecoderImpl(new PetlRegistry).parse(pipeline) shouldBe Right(expected)
  }


}
