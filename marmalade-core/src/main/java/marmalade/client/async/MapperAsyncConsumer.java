package marmalade.client.async;

import java.io.IOException;

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

public class MapperAsyncConsumer<T> extends AbstractAsyncResponseConsumer<T>
{

   private final Class<T> type;

   private T obj;

   private ObjectMapper mapper;

   public MapperAsyncConsumer(Class<T> type)
   {
      Preconditions.checkNotNull(type, "Type must not be null");
      this.type = type;
   }

   @Override
   protected T buildResult(HttpContext paramHttpContext) throws Exception
   {
      return obj;
   }

   @Override
   protected void onContentReceived(ContentDecoder contentDecoder, IOControl control) throws IOException
   {
      DecoderInputStream decoderInputStream = new DecoderInputStream(contentDecoder);
      obj = mapper.readValue(decoderInputStream, type);
      decoderInputStream.close();
   }

   @Override
   protected void onEntityEnclosed(HttpEntity entity, ContentType contentType) throws IOException
   {
      //
   }

   @Override
   protected void onResponseReceived(HttpResponse response) throws HttpException, IOException
   {
      String mime = MimeResolver.resolve(response);
      this.mapper = Registry.lookupMapper(mime);
   }

   @Override
   protected void releaseResources()
   {

   }

}
