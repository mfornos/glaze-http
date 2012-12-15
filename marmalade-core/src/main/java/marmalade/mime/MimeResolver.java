package marmalade.mime;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;

import com.google.common.base.Optional;

public final class MimeResolver
{

   private static final BasicHeader DEFAULT_HEADER = new BasicHeader("", ContentType.APPLICATION_JSON.getMimeType());

   public static String resolve(HttpResponse response)
   {
      Header contentType = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);

      String mime;

      if (contentType == null) {
         mime = resolveFromEntity(response);
      } else {
         mime = ContentType.parse(contentType.getValue()).getMimeType();
      }

      return mime;
   }

   private static String resolveFromEntity(HttpResponse response)
   {
      String mime = ContentType.APPLICATION_JSON.getMimeType();
      
      HttpEntity entity = response.getEntity();
      if (entity != null) {
         Header header = Optional.fromNullable(entity.getContentType()).or(DEFAULT_HEADER);
         mime = ContentType.parse(header.getValue()).getMimeType();
      }

      return mime;
   }

}
