package marmalade.client.async;

import java.io.IOException;

import org.apache.http.ContentTooLongException;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.nio.util.SimpleInputBuffer;
import org.apache.http.protocol.HttpContext;

public abstract class AbstractContentConsumer<T> extends AbstractAsyncResponseConsumer<T>
{

   private static final long MAX_CONTENT_LEN = 2147483647L;

   private volatile SimpleInputBuffer buf;

   @Override
   protected T buildResult(HttpContext paramHttpContext) throws Exception
   {
      return onBufferCompleted(this.buf);
   }

   abstract protected T onBufferCompleted(SimpleInputBuffer buf) throws IOException;

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
   }

   @Override
   protected void releaseResources()
   {
      this.buf = null;
   }

}
