package glaze.client.async;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.mime.MultipartEntity;

// XXX temporal, we want a zero copy multipart
public class BufferedMultipartEntity extends HttpEntityWrapper
{
   private final byte[] buffer;

   public BufferedMultipartEntity(MultipartEntity entity) throws IOException
   {
      super(entity);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
         entity.writeTo(baos);
         this.buffer = baos.toByteArray();
      } finally {
         baos.close();
      }
   }

   public long getContentLength()
   {
      if (this.buffer != null) {
         return this.buffer.length;
      }
      return this.wrappedEntity.getContentLength();
   }

   public InputStream getContent() throws IOException
   {
      if (this.buffer != null) {
         return new ByteArrayInputStream(this.buffer);
      }
      return this.wrappedEntity.getContent();
   }

   public boolean isChunked()
   {
      return ((this.buffer == null) && (this.wrappedEntity.isChunked()));
   }

   public boolean isRepeatable()
   {
      return true;
   }

   public void writeTo(OutputStream outstream) throws IOException
   {
      if (outstream == null) {
         throw new IllegalArgumentException("Output stream may not be null");
      }
      if (this.buffer != null)
         outstream.write(this.buffer);
      else
         this.wrappedEntity.writeTo(outstream);
   }

   public boolean isStreaming()
   {
      return ((this.buffer == null) && (this.wrappedEntity.isStreaming()));
   }
}
