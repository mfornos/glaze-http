
package marmalade.examples

import glaze.Glaze._
import glaze.client.UriBuilder._
import org.apache.http.HttpResponse
import glaze.client.Response
import glaze.scala.GlazeHelpers
import com.fasterxml.jackson.annotation._
import glaze.examples.mashape.MashapeClient
import java.util.Date

object MashapeApp extends GlazeHelpers {

  class Forecast @JsonCreator() (
    @JsonProperty("day_of_week") val dayOfWeek: String,
    @JsonProperty("low") val low: Integer, // Fahrenheit
    @JsonProperty("high") val high: Integer,
    @JsonProperty("condition") val condition: String) {
    override def toString: String = "%s L%s° H%s°F - %s\n".format(dayOfWeek, low, high, condition)
  }

  def main(args: Array[String]): Unit = {

    val mcli = new MashapeClient
    val where = "Barcelona"
    val uri = uriBuilderFrom("https://george-vustrey-weather.p.mashape.com/api.php").addParameter("_method", "getForecasts").addParameter("location", where).build

    try {
      val forecasts = Get(uri).withErrorHandler { r: Response =>
        println("Error: %s".format(r))
      }.mapAsync(mcli, classOf[Array[Forecast]])

      println("%s @%s\n".format(where, new Date))

      forecasts.get.foreach(forecast => println(forecast))

    } finally { mcli.shutdown }

  }

}