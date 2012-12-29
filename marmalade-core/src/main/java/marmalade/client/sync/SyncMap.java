package marmalade.client.sync;

import marmalade.client.MapRequest;
import marmalade.client.handlers.ErrorHandler;
import marmalade.client.handlers.MapperResponseHandler;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HttpContext;

public class SyncMap<T> extends MapRequest<T>
{

   public SyncMap(HttpUriRequest request, Class<T> type)
   {
      super(request, type);
   }

   public SyncMap(String namespace, HttpUriRequest request, Class<T> type, ErrorHandler errorHandler)
   {
      super(namespace, request, type, errorHandler);
   }

   public SyncMap(String namespace, HttpUriRequest request, Class<T> type, ErrorHandler errorHandler,
         ContentType overrideType)
   {
      super(namespace, request, type, errorHandler, overrideType);
   }

   public SyncMap(String namespace, HttpUriRequest request, Class<T> type, HttpContext context,
         ErrorHandler errorHandler)
   {
      super(namespace, request, type, context, errorHandler);
   }

   public SyncMap(String namespace, HttpUriRequest request, Class<T> type, HttpContext context,
         ErrorHandler errorHandler, ContentType overrideType)
   {
      super(namespace, request, type, context, errorHandler, overrideType);
   }

   public MapperResponseHandler<T> getHandler()
   {
      return new MapperResponseHandler<T>(namespace, type, errorHandler, overrideType);
   }

}
