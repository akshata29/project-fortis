package com.microsoft.partnercatalyst.fortis.spark.transforms.locations.client

import java.io.IOException

import com.microsoft.partnercatalyst.fortis.spark.transforms.locations.{Geofence, Logger}
import com.microsoft.partnercatalyst.fortis.spark.transforms.locations.dto.{FeatureServiceFeature, FeatureServiceResponse}
import net.liftweb.json
import net.liftweb.json.JsonParser.ParseException

import scala.io.Source

@SerialVersionUID(100L)
class FeatureServiceClient(host: String) extends Serializable with Logger {
  def bbox(geofence: Geofence): Iterable[FeatureServiceFeature] = {
    parseResponse(fetchBboxResponse(geofence))
  }

  def point(latitude: Double, longitude: Double): Iterable[FeatureServiceFeature] = {
    parseResponse(fetchPointResponse(latitude = latitude, longitude = longitude))
  }

  def name(names: Iterable[String]): Iterable[FeatureServiceFeature] = {
    parseResponse(fetchNameResponse(names))
  }

  private def parseResponse(response: String): Iterable[FeatureServiceFeature] = {
    implicit val formats = json.DefaultFormats

    try {
      json.parse(response).extract[FeatureServiceResponse].features
    } catch {
      case ex: ParseException =>
        logError(s"Unable to parse feature service response: $response", ex)
        List()
    }
  }

  protected def fetchBboxResponse(geofence: Geofence): String = {
    val fetch = s"http://$host/features/bbox/${geofence.north}/${geofence.west}/${geofence.south}/${geofence.east}"
    fetchResponse(fetch)
  }

  protected def fetchPointResponse(latitude: Double, longitude: Double): String = {
    val fetch = s"http://$host/features/point/$latitude/$longitude"
    fetchResponse(fetch)
  }

  protected def fetchNameResponse(names: Iterable[String]): String = {
    val fetch = s"http://$host/features/name/${names.mkString(",")}"
    fetchResponse(fetch)
  }

  private def fetchResponse(url: String): String = {
    try {
      Source.fromURL(url)("UTF-8").mkString
    } catch {
      case ex: IOException =>
        logError(s"Unable to fetch feature service url: $url", ex)
        ""
    }
  }
}
