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

object SimpleApp extends AnyRef with MarmaladeHelpers {

  val googleKey = "YOUR GOOGLE KEY";

  def main(args: Array[String]): Unit = {

    // Register scala module
    Registry.lookupMapper(ContentType.APPLICATION_JSON).registerModule(DefaultScalaModule);

    // JSON results from Freebase
    //

    val uri = uriBuilderFrom("https://www.googleapis.com/freebase/v1/mqlread").addParameter("query", "{\"id\":\"/en/google\",\"name\":null}").addParameter("key", googleKey).build
    val result = Get(uri).withErrorHandler((e: HttpResponse) => println("Error: %s".format(e))).map(typeRef[Map[String, String]]);
    println("Freebase result: %s".format(result("result")))

    // Simple GET with handler
    //

    Get("http://www.yahoo.com/").withHandler((r: HttpResponse) => {
      println("Handling: %s".format(r))
    }).send[Any]

    // Simple GET without handler
    //

    Get("http://ask.com/").send[Response].status match {
      case 200 => println("OK")
      case x if x > 200 => println(x)
      case _ => println("WTF?")
    }

  }

}