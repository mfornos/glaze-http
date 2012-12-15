package marmalade.client.wire.tasks;

import java.io.Serializable;

import marmalade.client.sync.SyncClient;

import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapCall<T extends Serializable> extends CallableRequest<T>
{
   private static final long serialVersionUID = 2241780553418194274L;

   private static final Logger LOGGER = LoggerFactory.getLogger(SendCall.class);

   private final Class<T> type;

   public MapCall(HttpUriRequest request, Class<T> type)
   {
      this(request, null, type);
      setCallback(emptyCallback());
   }

   public MapCall(HttpUriRequest request, SerializableResponseCallback<T> callback, Class<T> type)
   {
      super(request, null);
      setCallback(callback);
      this.type = type;
   }

   @Override
   protected T execute(SyncClient client, HttpUriRequest request)
   {
      return client.map(request, type);
   }

   private SerializableResponseCallback<T> emptyCallback()
   {
      return new SerializableResponseCallback<T>()
      {
         private static final long serialVersionUID = 5568234522090415157L;

         @Override
         public T onException(Exception e)
         {
            LOGGER.error(e.getMessage(), e);
            return null;
         }

         @Override
         public T onResponse(T bean)
         {
            if (LOGGER.isDebugEnabled())
               LOGGER.debug("Provide a callback and do something interesting with {}", bean);
            return bean;
         }
      };
   }

}
