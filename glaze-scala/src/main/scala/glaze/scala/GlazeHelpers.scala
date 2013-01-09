package glaze.scala

import com.fasterxml.jackson.core.`type`.TypeReference
import java.lang.reflect.Type
import java.lang.reflect.ParameterizedType
import glaze.client.handlers.ErrorHandler
import glaze.client.Response
import glaze.client.handlers.DefaultResponseHandler
import org.apache.http.HttpResponse
import org.apache.http.client.ResponseHandler

// TODO see https://github.com/jsuereth/scala-arm
// Automatic-Resource-Management (endAsync, etc)
trait GlazeHelpers {

  class ErrorHandlerWrap(e: Response => Unit)
    extends ErrorHandler {
    override def onError(error: Response) = e(error)
  }

  implicit def ErrorHandlerImplicit(r: Response => Unit) = new ErrorHandlerWrap(r)

  class ResponseHandlerWrap[T](e: HttpResponse => T)
    extends ResponseHandler[T] {
    override def handleResponse(response: HttpResponse) = e(response)
  }

  implicit def ResponseHandlerImplicit[T](r: HttpResponse => T) = new ResponseHandlerWrap[T](r)

  def typeRef[T: Manifest] = new TypeReference[T] {
    override def getType = typeFromManifest(manifest[T])
  }

  private def typeFromManifest(m: Manifest[_]): Type = {
    if (m.typeArguments.isEmpty) { m.erasure }
    else new ParameterizedType {
      override def getRawType = m.erasure

      override def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray

      override def getOwnerType = null
    }
  }

}