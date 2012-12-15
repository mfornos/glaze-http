package marmalade.examples

import marmalade.Marmalade._
import marmalade.client.handlers.ErrorHandler
import org.apache.http.HttpResponse
import org.apache.http.client.ResponseHandler

object SimpleApp {

  class ErrorHandlerWrap(e: HttpResponse => Unit)
    extends ErrorHandler {
    def onError(error: HttpResponse) = e(error)
  }
  implicit def ErrorHandlerImplicit(r: HttpResponse => Unit) = new ErrorHandlerWrap(r)

  class ResponseHandlerWrap[T](e: HttpResponse => T)
    extends ResponseHandler[T] {

    def handleResponse(response: HttpResponse) = e(response)
  }
  implicit def ResponseHandlerImplicit[T](r: HttpResponse => T) = new ResponseHandlerWrap[T](r)

  def main(args: Array[String]): Unit = {

    val response = Get("http://www.yahoo.com/879854794").withErrorHandler((e: HttpResponse) => println("error" + e)).map()

    val httpResponse: HttpResponse = Get("http://www.yahoo.com/879854794").withHandler((r: HttpResponse) => {
      println("handle" + r)
      r;
    }).send()

    println("Hello, world! " + response)
    println("Hello, world! " + httpResponse)
  }

}