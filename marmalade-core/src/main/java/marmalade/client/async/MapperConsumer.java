package marmalade.client.async;

import java.io.IOException;
import java.nio.ByteBuffer;

import marmalade.client.Response;
import marmalade.client.handlers.CroakErrorHandler;
import marmalade.client.handlers.ErrorHandler;
import marmalade.mime.MimeResolver;
import marmalade.spi.Registry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

public class MapperConsumer<T> extends AbstractAsyncResponseConsumer<T>
{

   private static final CroakErrorHandler EH = new CroakErrorHandler();

   private final Class<T> type;

   private final ErrorHandler errorHandler;

   private volatile T obj;

   private volatile Response response;

   private volatile ObjectMapper mapper;

   public MapperConsumer(Class<T> type)
   {
      this(type, EH);
   }

   public MapperConsumer(Class<T> type, ErrorHandler errorHandler)
   {
      Preconditions.checkNotNull(type, "Type must not be null");
      this.type = type;
      this.errorHandler = errorHandler;
   }

   @Override
   protected T buildResult(HttpContext paramHttpContext) throws Exception
   {
      return obj;
   }

   @Override
   protected void onContentReceived(ContentDecoder contentDecoder, IOControl control) throws IOException
   {
      if (mapper == null) {
         consume(contentDecoder);
         return;
      }

      DecoderInputStream decoderInputStream = new DecoderInputStream(contentDecoder);
      try {
         obj = mapper.readValue(decoderInputStream, type);
      } finally {
         decoderInputStream.close();
      }
   }

   @Override
   protected void onEntityEnclosed(HttpEntity entity, ContentType contentType) throws IOException
   {
      //
   }

   @Override
   protected void onResponseReceived(HttpResponse httpResponse) throws HttpException, IOException
   {
      response = new Response(httpResponse);

      if (response.isError()) {
         errorHandler.onError(response);
         return;
      }

      String mime = MimeResolver.resolve(httpResponse);
      this.mapper = Registry.lookupMapper(mime);
   }

   @Override
   protected void releaseResources()
   {
      this.mapper = null;
      this.response = null;
      this.obj = null;
   }

   private void consume(ContentDecoder contentDecoder) throws IOException
   {
      ByteBuffer bbuf = ByteBuffer.allocate(8192);
      while (true) {
         int bytesRead = contentDecoder.read(bbuf);
         if (bytesRead <= 0) {
            return;
         }
         bbuf.clear();
      }
   }

}
