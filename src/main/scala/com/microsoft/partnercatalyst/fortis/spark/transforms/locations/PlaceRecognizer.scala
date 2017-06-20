package com.microsoft.partnercatalyst.fortis.spark.transforms.locations

import com.microsoft.partnercatalyst.fortis.spark.logging.Loggable
import com.microsoft.partnercatalyst.fortis.spark.transforms.entities.EntityRecognizer
import com.microsoft.partnercatalyst.fortis.spark.transforms.nlp.OpeNER
import com.microsoft.partnercatalyst.fortis.spark.transforms.nlp.OpeNER.entityIsPlace

@SerialVersionUID(100L)
class PlaceRecognizer(
  modelsSource: Option[String] = None,
  enabledLanguages: Set[String] = OpeNER.EnabledLanguages
) extends Serializable with Loggable {

  @volatile private lazy val entityRecognizer = createEntityRecognizer()

  def extractPlaces(text: String, language: String): Set[String] = {
    entityRecognizer.extractEntities(text, language).filter(entityIsPlace).map(_.getStr).toSet
  }

  protected def createEntityRecognizer(): EntityRecognizer = {
    new EntityRecognizer(modelsSource, enabledLanguages)
  }
}
