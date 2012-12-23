package marmalade.client.async;

import java.io.IOException;

import marmalade.client.Response;
import marmalade.client.handlers.CroakErrorHandler;
import marmalade.client.handlers.ErrorHandler;
import marmalade.mime.MimeResolver;
import marmalade.spi.Registry;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.ContentInputStream;
import org.apache.http.nio.util.SimpleInputBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class MapperConsumer<T> extends AbstractContentConsumer<T>
{

   private static final Logger LOGGER = LoggerFactory.getLogger(MapperConsumer.class);

   private final Class<T> type;

   private final ErrorHandler errorHandler;

   private volatile ObjectMapper mapper;

   private final String namespace;

   private ContentType overrideType;

   public MapperConsumer(Class<T> type)
   {
      this(Registry.NS_DEFAULT, type, CroakErrorHandler.instance());
   }

   public MapperConsumer(Class<T> type, ErrorHandler errorHandler)
   {
      this(Registry.NS_DEFAULT, type, errorHandler);
   }

   public MapperConsumer(String namespace, Class<T> type, ErrorHandler errorHandler)
   {
      this(namespace, type, errorHandler, null);
   }

   public MapperConsumer(String namespace, Class<T> type, ErrorHandler errorHandler, ContentType overrideType)
   {
      Preconditions.checkNotNull(type, "Type must not be null");

      this.type = type;
      this.overrideType = overrideType;
      this.errorHandler = errorHandler == null ? CroakErrorHandler.instance() : errorHandler;
      this.namespace = Optional.fromNullable(namespace).or(Registry.NS_DEFAULT);
   }

   @Override
   protected T onBufferCompleted(SimpleInputBuffer buffer) throws IOException
   {
      if (mapper == null) {
         buffer.shutdown();
         return null;
      }

      return mapper.readValue(new ContentInputStream(buffer), type);
   }

   @Override
   protected void onResponseReceived(HttpResponse httpResponse) throws HttpException, IOException
   {
      Response response = new Response(httpResponse);

      if (response.isError()) {
         errorHandler.onError(response);
         return;
      }

      String mime = overrideType == null ? MimeResolver.resolve(httpResponse) : overrideType.getMimeType();
      this.mapper = Registry.lookupMapper(namespace, mime);

      if (mapper == null) {
         LOGGER.warn("Mapper not found for response {}. Consuming quietly.", httpResponse);
      }
   }

   @Override
   protected void releaseResources()
   {
      this.mapper = null;
   }

}
