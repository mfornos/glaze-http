package marmalade.examples

import marmalade.Marmalade._
import marmalade.spi.Registry
import org.apache.http.entity.ContentType
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import marmalade.client.UriBuilder._
import scala.collection.mutable
import org.apache.http.HttpResponse
import marmalade.client.Response
import marmalade.client.handlers.ErrorHandler
import marmalade.scala.MarmaladeHelpers
import marmalade.client.handlers.DefaultResponseHandler
import org.apache.http.util.EntityUtils

object SimpleApp extends MarmaladeHelpers {

  def main(args: Array[String]): Unit = {

    // Register scala module
    Registry.lookupMapper(ContentType.APPLICATION_JSON).registerModule(DefaultScalaModule)

    // Simple GET with generic handler
    //
    Get("http://www.opera.com/").withHandler { r: HttpResponse =>
      println("Handling: %s".format(r))
      EntityUtils.consumeQuietly(r.getEntity)
    }.send[Any]

    // Simple GET with handler
    //
    Get("http://en.wikipedia.org/wiki/Marmalade").withHandler(new DefaultResponseHandler {
      def onError(e: Response): Response = {
        println("Error: %s".format(e))
        null
      }
      def onResponse(r: Response): Response = {
        println("Ok: %s".format(r))
        r
      }
    }).send[Response].discardContent

    // Simple GET without handler
    //
    Get("http://ask.com/").send[Response].discardContent.status match {
      case 200 => println("OK")
      case x if x > 200 => println(x)
      case _ => println("WTF?")
    }

    // JSON results from Freebase
    //
    // freeBase("YOUR GOOGLE KEY")

  }

  def freeBase(k: String) {
    val uri = uriBuilderFrom("https://www.googleapis.com/freebase/v1/mqlread").addParameter("query", "{\"id\":\"/en/google\",\"name\":null}").addParameter("key", k).build
    val result = Get(uri).withErrorHandler { e: Response => println("Error: %s".format(e)) }.map(typeRef[Map[String, String]]);
    println("Freebase result: %s".format(result("result")))
  }

}