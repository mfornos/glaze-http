package marmalade.client.async;

import marmalade.client.MapRequest;
import marmalade.client.handlers.ErrorHandler;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HttpContext;

public class AsyncMap<T> extends MapRequest<T>
{

   private FutureCallback<T> futureCallback;

   public AsyncMap(HttpUriRequest request, Class<T> type)
   {
      super(request, type);
   }

   public AsyncMap(String namespace, HttpUriRequest request, Class<T> type, ErrorHandler errorHandler)
   {
      super(namespace, request, type, errorHandler);
   }

   public AsyncMap(String namespace, HttpUriRequest request, Class<T> type, ErrorHandler errorHandler,
         ContentType overrideType)
   {
      super(namespace, request, type, errorHandler, overrideType);
   }

   public AsyncMap(String namespace, HttpUriRequest request, Class<T> type, FutureCallback<T> callback,
         ErrorHandler errorHandler)
   {
      this(namespace, request, type, null, callback, errorHandler, null);
   }

   public AsyncMap(String namespace, HttpUriRequest request, Class<T> type, HttpContext context,
         ErrorHandler errorHandler)
   {
      super(namespace, request, type, context, errorHandler);
   }

   public AsyncMap(String namespace, HttpUriRequest request, Class<T> type, HttpContext context,
         ErrorHandler errorHandler, ContentType overrideType)
   {
      super(namespace, request, type, context, errorHandler, overrideType);
   }

   public AsyncMap(String namespace, HttpUriRequest request, Class<T> type, HttpContext context,
         FutureCallback<T> callback, ErrorHandler errorHandler)
   {
      this(namespace, request, type, context, callback, errorHandler, null);
   }

   public AsyncMap(String namespace, HttpUriRequest request, Class<T> type, HttpContext context,
         FutureCallback<T> callback, ErrorHandler errorHandler, ContentType overrideType)
   {
      this.type = type;
      this.errorHandler = errorHandler;
      this.namespace = namespace;
      this.request = request;
      this.futureCallback = callback;
      this.overrideType = overrideType;
   }

   public MapperConsumer<T> getConsumer()
   {
      return new MapperConsumer<T>(namespace, type, errorHandler, overrideType);
   }

   public FutureCallback<T> getFutureCallback()
   {
      return futureCallback;
   }

}
