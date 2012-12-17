package marmalade.client;

import java.io.IOException;
import java.io.InputStream;

import marmalade.MarmaladeException;
import marmalade.func.Closures.ResponseClosure;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
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
      if (isEmpty()) {
         return null;
      }
      try {
         return EntityUtils.toByteArray(this.wrapped.getEntity());
      } catch (IOException e) {
         throw new MarmaladeException(e);
      }
   }

   public InputStream asInputStream()
   {
      if (isEmpty()) {
         return null;
      }
      try {
         return this.wrapped.getEntity().getContent();
      } catch (IOException e) {
         throw new MarmaladeException(e);
      }
   }

   public String asString()
   {
      if (isEmpty()) {
         return null;
      }
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

   public boolean isEmpty()
   {
      return wrapped.getEntity() == null;
   }

   public boolean isError()
   {
      return wrapped.getStatusLine().getStatusCode() >= 300;
   }

   public boolean isNotError()
   {
      return !isError();
   }

   public boolean isOk()
   {
      return status() == HttpStatus.SC_OK;
   }

   public int status()
   {
      return wrapped.getStatusLine().getStatusCode();
   }

   public StatusLine statusLine()
   {
      return wrapped.getStatusLine();
   }

   @Override
   public String toString()
   {
      return "Response [wrapped=" + wrapped + "]";
   }

   public <T> T with(ResponseClosure<T, Response> closure)
   {
      return closure.on(this);
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#finalize()
    */
   protected void finalize() throws Throwable
   {
      if (wrapped != null) {
         discardContent();
      }
      super.finalize();
   }
}
