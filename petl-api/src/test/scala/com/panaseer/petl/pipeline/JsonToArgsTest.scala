package com.panaseer.petl.pipeline

import org.scalatest.{FlatSpec, Matchers}

class JsonToArgsTest extends FlatSpec with Matchers {

  "Parsing json" should "work" in {

    val yml =
      """
        |name: Common Ingestion
        |vars:
        |stages:
        |    - name: Extracting from Hive
        |      hiveExtractor:
        |          inputTable: "${inputTableName}"
        |
        |    - name: "Rename something to bar"
        |      transformer:
        |        operations:
        |          - rename:
        |             column: 'something'
        |             to: 'foo'
        |          - rename:
        |              column: 'foo'
        |              to: 'bar'
        |
        |    - name: "Write data to hive"
        |      hiveWriter:
        |        outputTable: '${outputTableName}'
        |
    """.stripMargin

    val extractFromHive = PetlObject(Map(
      "name" -> PetlString("Extracting from Hive"),
      "hiveExtractor" -> PetlObject(Map(
        "inputTable" -> PetlString("${inputTableName}")
      ))
    ))
    val renameSomethingToFoo = PetlObject(Map(
      "rename" -> PetlObject(Map("column" -> PetlString("something"), "to" -> PetlString("foo")))
    ))
    val renameFooToBar = PetlObject(Map(
      "rename" -> PetlObject(Map("column" -> PetlString("foo"), "to" -> PetlString("bar")))
    ))
    val renameToFoo = PetlObject(Map(
      "name" -> PetlString("Rename something to bar"),
      "transformer" -> PetlObject(Map("operations" -> PetlArray(Seq(renameSomethingToFoo, renameFooToBar))
      ))
    ))
    val writeToHive = PetlObject(Map(
      "name" -> PetlString("Write data to hive"),
      "hiveWriter" -> PetlObject(Map("outputTable" -> PetlString("${outputTableName}")))
    ))

    val expected: PetlArg = PetlObject(Map[String, PetlArg](
      "name" -> PetlString("Common Ingestion"),
      "vars" -> PetlNone,
      "stages" -> PetlArray(Seq(extractFromHive, renameToFoo, writeToHive))
    ))

    val json = io.circe.yaml.parser.parse(yml).right.get
    new JsonToArgs().parse(json) shouldEqual expected
  }
}
