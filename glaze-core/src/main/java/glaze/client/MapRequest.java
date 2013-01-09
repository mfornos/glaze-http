package glaze.client;

import glaze.client.handlers.ErrorHandler;
import glaze.spi.Registry;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HttpContext;

public class MapRequest<T>
{
   protected String namespace;
   protected HttpUriRequest request;
   protected Class<T> type;
   protected HttpContext context;
   protected ErrorHandler errorHandler;
   protected ContentType overrideType;

   public MapRequest()
   {
      this.namespace = Registry.NS_DEFAULT;
   }

   public MapRequest(HttpUriRequest request, Class<T> type)
   {
      this(Registry.NS_DEFAULT, request, type, null, null, null);
   }

   public MapRequest(String namespace, HttpUriRequest request, Class<T> type, ErrorHandler errorHandler)
   {
      this(namespace, request, type, errorHandler, null);
   }

   public MapRequest(String namespace, HttpUriRequest request, Class<T> type, ErrorHandler errorHandler,
         ContentType overrideType)
   {
      this(namespace, request, type, null, errorHandler, overrideType);
   }

   public MapRequest(String namespace, HttpUriRequest request, Class<T> type, HttpContext context,
         ErrorHandler errorHandler)
   {
      this(namespace, request, type, context, errorHandler, null);
   }

   public MapRequest(String namespace, HttpUriRequest request, Class<T> type, HttpContext context,
         ErrorHandler errorHandler, ContentType overrideType)
   {
      this.type = type;
      this.errorHandler = errorHandler;
      this.namespace = namespace;
      this.request = request;
      this.overrideType = overrideType;
   }

   public HttpContext getContext()
   {
      return context;
   }

   public ErrorHandler getErrorHandler()
   {
      return errorHandler;
   }

   public String getNamespace()
   {
      return namespace;
   }

   public HttpUriRequest getRequest()
   {
      return request;
   }

   public Class<T> getType()
   {
      return type;
   }

   public boolean hasContext()
   {
      return context != null;
   }

   public void setContext(HttpContext context)
   {
      this.context = context;
   }

   public void setErrorHandler(ErrorHandler errorHandler)
   {
      this.errorHandler = errorHandler;
   }

   public void setNamespace(String namespace)
   {
      this.namespace = namespace;
   }

   public void setRequest(HttpUriRequest request)
   {
      this.request = request;
   }

   public void setType(Class<T> type)
   {
      this.type = type;
   }
}
