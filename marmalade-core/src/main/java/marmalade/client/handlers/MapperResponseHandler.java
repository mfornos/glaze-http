package marmalade.client.handlers;

import java.io.IOException;
import java.io.InputStream;

import marmalade.MarmaladeException;
import marmalade.client.Response;
import marmalade.mime.MimeResolver;
import marmalade.spi.Registry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * Response handler that maps the content of the response to a bean according to
 * the content-type header. [TODO Explain content-type lookup strategy]
 * 
 * @param <T>
 *           The mapper return type
 */
public class MapperResponseHandler<T> implements ResponseHandler<T>
{

   private static final Logger LOGGER = LoggerFactory.getLogger(MapperResponseHandler.class);

   private final Class<T> type;

   private final ContentType override;

   private final ErrorHandler errorHandler;

   private final String namespace;

   public MapperResponseHandler(Class<T> type)
   {
      this(Registry.NS_DEFAULT, type, CroakErrorHandler.instance(), null);
   }

   public MapperResponseHandler(Class<T> type, ContentType override)
   {
      this(Registry.NS_DEFAULT, type, CroakErrorHandler.instance(), override);
   }

   public MapperResponseHandler(Class<T> type, ErrorHandler errorHandler)
   {
      this(Registry.NS_DEFAULT, type, errorHandler, null);
   }

   public MapperResponseHandler(String namespace, Class<T> type)
   {
      this(namespace, type, CroakErrorHandler.instance(), null);
   }

   public MapperResponseHandler(String namespace, Class<T> type, ContentType override)
   {
      this(namespace, type, CroakErrorHandler.instance(), override);
   }

   public MapperResponseHandler(String namespace, Class<T> type, ErrorHandler errorHandler)
   {
      this(namespace, type, errorHandler, null);
   }

   public MapperResponseHandler(String namespace, Class<T> type, ErrorHandler errorHandler, ContentType override)
   {
      Preconditions.checkNotNull(type, "Type must not be null");

      this.type = type;
      this.override = override;
      this.errorHandler = errorHandler == null ? CroakErrorHandler.instance() : errorHandler;
      this.namespace = Optional.fromNullable(namespace).or(Registry.NS_DEFAULT);
   }

   @Override
   public T handleResponse(HttpResponse response) throws IOException
   {
      StatusLine statusLine = response.getStatusLine();
      return statusLine.getStatusCode() >= 300 ? error(response) : ok(response);
   }

   protected T error(HttpResponse httpResponse)
   {
      LOGGER.error("Got error {}", httpResponse);

      Response response = new Response(httpResponse);
      try {
         errorHandler.onError(response);
      } finally {
         response.discardContent();
      }

      return null;
   }

   protected boolean isOverriden()
   {
      return override != null;
   }

   protected T ok(HttpResponse response) throws IOException
   {
      InputStream is = null;
      try {
         String contentType = isOverriden() ? override.getMimeType() : MimeResolver.resolve(response);
         ObjectMapper mapper = Registry.lookupMapper(namespace, contentType);

         HttpEntity entity = response.getEntity();
         is = entity.getContent();

         if (mapper == null) {
            throw new MarmaladeException(String.format("Unable to resolve mapper for type '%s' in namespace '%s'", contentType, namespace));
         }

         return mapper.readValue(is, type);

      } finally {
         if (is != null)
            is.close();
      }
   }

}
