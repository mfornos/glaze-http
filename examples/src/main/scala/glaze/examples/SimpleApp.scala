package glaze.examples

import scala.collection.mutable

import glaze.Glaze._
import glaze.client.UriBuilder._

import org.apache.http.entity.ContentType
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import glaze.client.Response
import glaze.client.handlers.ErrorHandler
import glaze.scala.GlazeHelpers
import glaze.client.handlers.DefaultResponseHandler

object SimpleApp extends GlazeHelpers {

  def main(args: Array[String]): Unit = {

    // Simple GET with generic handler
    //
    Get("http://www.opera.com/").withHandler { r: HttpResponse =>
      println("Handling: %s".format(r))
      EntityUtils.consumeQuietly(r.getEntity)
    }.execute

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
    }).execute[Response].discardContent

    // Simple GET without handler
    //
    Get("http://ask.com/").send.discardContent.status match {
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