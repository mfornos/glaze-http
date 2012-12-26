package marmalade.client.wire.tasks;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import marmalade.client.Response;

import org.apache.http.Header;

import com.google.common.base.Objects;

public class SerializableResponse implements Serializable
{
   private static final long serialVersionUID = 6522661301517225640L;

   private final byte[] bytes;
   private final int status;
   private final Map<String, String> headers;

   public SerializableResponse(Response response)
   {
      this.headers = new HashMap<String, String>();
      this.bytes = getBytes(response);
      this.status = response.status();

      initHeaders(response);
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

   @Override
   public String toString()
   {
      return Objects.toStringHelper(this).add("status", status).add("headers", headers).toString();
   }

   private byte[] getBytes(Response response)
   {
      try {
         return response.asBytes();
      } catch (Exception e) {
         return new byte[] {};
      }
   }

   private void initHeaders(Response response)
   {
      Header[] allHeaders = response.getHttpResponse().getAllHeaders();
      if (allHeaders != null) {
         for (Header h : allHeaders) {
            headers.put(h.getName(), h.getValue());
         }
      }
   }
}
