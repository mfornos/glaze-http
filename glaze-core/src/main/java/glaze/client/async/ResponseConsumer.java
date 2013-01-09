package glaze.client.async;

import glaze.client.Response;
import glaze.client.handlers.CroakErrorHandler;
import glaze.client.handlers.ErrorHandler;

import java.io.IOException;


import org.apache.http.ContentTooLongException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.ContentBufferEntity;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.nio.util.SimpleInputBuffer;
import org.apache.http.protocol.HttpContext;

public class ResponseConsumer extends AbstractAsyncResponseConsumer<Response>
{

   private static final long MAX_CONTENT_LEN = 2147483647L;

   private volatile Response response;
   private volatile SimpleInputBuffer buf;

   private final ErrorHandler errorHandler;

   public ResponseConsumer()
   {
      this(CroakErrorHandler.instance());
   }

   public ResponseConsumer(ErrorHandler errorHandler)
   {
      this.errorHandler = errorHandler == null ? CroakErrorHandler.instance() : errorHandler;
   }

   @Override
   protected Response buildResult(HttpContext paramHttpContext) throws Exception
   {
      return response;
   }

   @Override
   protected void onContentReceived(ContentDecoder decoder, IOControl ioctrl) throws IOException
   {
      if (this.buf == null) {
         throw new IllegalStateException("Content buffer is null");
      }
      this.buf.consumeContent(decoder);
   }

   @Override
   protected void onEntityEnclosed(HttpEntity entity, ContentType contentType) throws IOException
   {
      long len = entity.getContentLength();
      if (len > MAX_CONTENT_LEN) {
         throw new ContentTooLongException("Entity content is too long: " + len);
      }
      if (len < 0L) {
         len = 4096L;
      }
      this.buf = new SimpleInputBuffer((int) len, new HeapByteBufferAllocator());
      this.response.getHttpResponse().setEntity(new ContentBufferEntity(entity, this.buf));
   }

   @Override
   protected void onResponseReceived(HttpResponse httpResponse) throws HttpException, IOException
   {
      this.response = new Response(httpResponse);

      if (response.isError()) {
         errorHandler.onError(response);
      }
   }

   @Override
   protected void releaseResources()
   {
      this.response = null;
      this.buf = null;
   }

}
