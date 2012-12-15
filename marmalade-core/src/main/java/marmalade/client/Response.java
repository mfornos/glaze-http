package marmalade.client;

import java.io.IOException;
import java.io.InputStream;

import marmalade.MarmaladeException;
import marmalade.func.Closures.ResponseClosure;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * Wrapper for HttpResponse that provides some convenience methods.
 * 
 */
public class Response
{
   private final HttpResponse wrapped;

   public Response(HttpResponse response)
   {
      this.wrapped = response;
   }

   public byte[] asBytes()
   {
      try {
         return EntityUtils.toByteArray(this.wrapped.getEntity());
      } catch (IOException e) {
         throw new MarmaladeException(e);
      }
   }

   public InputStream asInputStream()
   {
      try {
         return this.wrapped.getEntity().getContent();
      } catch (IOException e) {
         throw new MarmaladeException(e);
      }
   }

   public String asString()
   {
      try {
         return EntityUtils.toString(this.wrapped.getEntity());
      } catch (IOException e) {
         throw new MarmaladeException(e);
      }
   }

   public Response discardContent()
   {
      EntityUtils.consumeQuietly(wrapped.getEntity());
      return this;
   }

   public HttpResponse getHttpResponse()
   {
      return wrapped;
   }

   public int getStatus()
   {
      return wrapped.getStatusLine().getStatusCode();
   }

   public boolean isError()
   {
      return wrapped.getStatusLine().getStatusCode() >= 300;
   }

   public boolean isNotError()
   {
      return !isError();
   }

   public <T> T with(ResponseClosure<T, Response> closure)
   {
      return closure.on(this);
   }
}
