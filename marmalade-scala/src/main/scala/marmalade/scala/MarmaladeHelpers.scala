package marmalade.scala

import com.fasterxml.jackson.core.`type`.TypeReference
import java.lang.reflect.Type
import java.lang.reflect.ParameterizedType
import org.apache.http.HttpResponse
import marmalade.client.handlers.ErrorHandler

import org.apache.http.client.ResponseHandler

trait MarmaladeHelpers {
  
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

  def typeRef[T: Manifest] = new TypeReference[T] {
    override def getType = typeFromManifest(manifest[T])
  }

  private def typeFromManifest(m: Manifest[_]): Type = {
    if (m.typeArguments.isEmpty) { m.erasure }
    else new ParameterizedType {
      def getRawType = m.erasure

      def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray

      def getOwnerType = null
    }
  }

}