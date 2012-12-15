package marmalade.client.async;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.http.nio.ContentDecoder;

/**
 * An InputStream that reads from a {@link ContentDecoder}.
 * 
 */
public class DecoderInputStream extends InputStream
{
   private final ContentDecoder decoder;
   private ByteBuffer bb = null;
   private byte[] bs = null; // Invoker's previous array
   private byte[] b1 = null;

   public DecoderInputStream(ContentDecoder decoder)
   {
      this.decoder = decoder;
   }

   @Override
   public int read() throws IOException
   {
      if (b1 == null)
         b1 = new byte[1];
      int n = this.read(b1);
      if (n == 1)
         return b1[0] & 0xff;
      return -1;
   }

   public synchronized int read(byte[] bs, int off, int len) throws IOException
   {
      if ((off < 0) || (off > bs.length) || (len < 0) || ((off + len) > bs.length) || ((off + len) < 0)) {
         throw new IndexOutOfBoundsException();
      } else if (len == 0)
         return 0;

      ByteBuffer bb = ((this.bs == bs) ? this.bb : ByteBuffer.wrap(bs));
      bb.position(off);
      bb.limit(Math.min(off + len, bb.capacity()));
      this.bb = bb;
      this.bs = bs;
      return read(bb);
   }

   protected int read(ByteBuffer bb) throws IOException
   {
      return decoder.read(bb);
   }

}
