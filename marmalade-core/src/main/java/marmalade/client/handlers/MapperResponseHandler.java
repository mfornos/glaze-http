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

/**
 * Response handler that maps the content of the response to a bean according to
 * the content-type header. [TODO Explain content-type lookup strategy]
 * 
 * @param <T>
 *           The mapper return type
 */
public class MapperResponseHandler<T> implements ResponseHandler<T>
{

   private static final ErrorHandler DEFAULT_EH = new CroakErrorHandler();

   private static final Logger LOGGER = LoggerFactory.getLogger(MapperResponseHandler.class);

   private final Class<T> type;

   private final ContentType override;

   private final ErrorHandler errorHandler;

   public MapperResponseHandler(Class<T> type)
   {
      this(type, DEFAULT_EH, null);
   }

   public MapperResponseHandler(Class<T> type, ContentType override)
   {
      this(type, DEFAULT_EH, override);
   }

   public MapperResponseHandler(Class<T> type, ErrorHandler errorHandler)
   {
      this(type, errorHandler, null);
   }

   public MapperResponseHandler(Class<T> type, ErrorHandler errorHandler, ContentType override)
   {
      this.type = type;
      this.override = override;
      this.errorHandler = Optional.fromNullable(errorHandler).or(DEFAULT_EH);
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
         ObjectMapper mapper = Registry.lookupMapper(contentType);

         HttpEntity entity = response.getEntity();
         is = entity.getContent();

         if (mapper == null) {
            throw new MarmaladeException(String.format("Unable to resolve mapper for type %s", contentType));
         }

         return mapper.readValue(is, type);

      } finally {
         if (is != null)
            is.close();
      }
   }

}
