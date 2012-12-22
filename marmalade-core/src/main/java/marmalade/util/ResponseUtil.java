package marmalade.util;

import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;

public final class ResponseUtil
{
   public static final String DEFAULT_ENCODING = "UTF-8";

   public static String resolveEncoding(HttpResponse response)
   {
      String encoding = null;

      HttpEntity entity = response.getEntity();
      Header encHead = entity.getContentEncoding();
      if (encHead == null) {
         ContentType ctype = ContentType.get(entity);
         encoding = fromContentType(ctype);
      } else {
         encoding = encHead.getValue();
      }

      if (encoding == null) {
         encoding = fromContentEncoding(response.getFirstHeader(HttpHeaders.CONTENT_ENCODING));
      }

      if (encoding == null) {
         Header ctHeader = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
         if (ctHeader != null) {
            encoding = fromContentType(ContentType.parse(ctHeader.getValue()));
         }
      }

      return encoding == null ? DEFAULT_ENCODING : encoding;
   }

   private static String fromContentEncoding(Header header)
   {
      return header == null ? null : header.getValue();
   }

   private static String fromContentType(ContentType ctype)
   {
      if (ctype == null) {
         return null;
      }

      Charset charset = ctype.getCharset();
      return charset == null ? null : charset.toString();
   }

}
