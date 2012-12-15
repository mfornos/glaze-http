package marmalade.client.wire.tasks;

import java.io.Serializable;
import java.util.Arrays;

import marmalade.client.Response;

// TODO complete fields: content-type
public class SerializableResponse implements Serializable
{
   private static final long serialVersionUID = 6522661301517225640L;

   private final byte[] bytes;
   private int status;

   public SerializableResponse(Response response)
   {
      bytes = response.asBytes();
      status = response.status();
   }

   public String asString()
   {
      return new String(bytes);
   }

   public byte[] getBytes()
   {
      return bytes;
   }

   public int getStatus()
   {
      return status;
   }

   public void setStatus(int status)
   {
      this.status = status;
   }

   @Override
   public String toString()
   {
      return "SerializableResponse [content=" + Arrays.toString(bytes) + ", status=" + status + "]";
   }
}
