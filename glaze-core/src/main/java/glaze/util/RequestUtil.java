package glaze.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;

public class RequestUtil
{

   public static HttpEntity getEntity(HttpRequestBase request)
   {
      return (isEnclosingEntity(request)) ? asEntityEnclosing(request).getEntity() : null;
   }

   public static boolean isEnclosingEntity(HttpUriRequest request)
   {
      return HttpEntityEnclosingRequest.class.isAssignableFrom(request.getClass());
   }

   public static HttpEntityEnclosingRequest setEntity(HttpUriRequest request, HttpEntity entity)
   {
      HttpEntityEnclosingRequest entityEnclosing = asEntityEnclosing(request);
      entityEnclosing.setEntity(entity);
      return entityEnclosing;
   }

   private static HttpEntityEnclosingRequest asEntityEnclosing(HttpUriRequest request)
   {
      return (HttpEntityEnclosingRequest) request;
   }

}
