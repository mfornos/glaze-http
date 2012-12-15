package marmalade.client.tasks;

import marmalade.client.sync.SyncClient;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapRequest<T> extends ExecutableRequest<T>
{

   private static final Logger LOGGER = LoggerFactory.getLogger(ExecRequest.class);
   private final Class<T> type;

   public MapRequest(SyncClient client, HttpUriRequest request, Class<T> type)
   {
      super(client, request, null);
      setCallback(emptyCallback());
      this.type = type;
   }

   @Override
   protected T execute(SyncClient client, HttpUriRequest request, HttpContext context)
   {
      return client.map(request, context, type);
   }

   private RunnableRequestCallback<T> emptyCallback()
   {
      return new RunnableRequestCallback<T>()
      {
         @Override
         public void onException(Exception e)
         {
            LOGGER.error(e.getMessage(), e);
         }

         @Override
         public void onResponse(T bean)
         {
            if (LOGGER.isDebugEnabled())
               LOGGER.debug("Provide a callback and do something interesting with {}", bean);
         }
      };
   }

}
