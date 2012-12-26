package marmalade.test.http;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class SerializableResponse implements Serializable
{
   private static final long serialVersionUID = 6522661301517225640L;

   private final byte[] bytes;
   private final int status;
   private final Map<String, String> headers;

   public SerializableResponse(HttpResponse response)
   {
      this.headers = new HashMap<String, String>();
      this.bytes = getBytes(response);
      this.status = response.getStatusLine().getStatusCode();

      Header[] allHeaders = response.getAllHeaders();
      for (Header h : allHeaders) {
         headers.put(h.getName(), h.getValue());
      }
   }

   public String asString()
   {
      return new String(bytes);
   }

   public byte[] getBytes()
   {
      return bytes;
   }

   public Map<String, String> getHeaders()
   {
      return Collections.unmodifiableMap(headers);
   }

   public int status()
   {
      return status;
   }

   private byte[] getBytes(HttpResponse response)
   {
      try {
         return EntityUtils.toByteArray(response.getEntity());
      } catch (Exception e) {
         return new byte[] {};
      }
   }
}
