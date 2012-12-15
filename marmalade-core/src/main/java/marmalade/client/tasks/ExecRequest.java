package marmalade.client.tasks;

import marmalade.client.Response;
import marmalade.client.sync.SyncClient;

import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecRequest extends ExecutableRequest<Response>
{

   private static final Logger LOGGER = LoggerFactory.getLogger(ExecRequest.class);

   private static final RunnableRequestCallback<Response> EMPTY_CALLBACK = new RunnableRequestCallback<Response>()
   {
      @Override
      public void onException(Exception e)
      {
         LOGGER.error(e.getMessage(), e);
      }

      @Override
      public void onResponse(Response response)
      {
         if (LOGGER.isDebugEnabled())
            LOGGER.debug("Provide a callback and do something interesting with {}", response);
      }
   };

   public ExecRequest(SyncClient client, HttpUriRequest request)
   {
      super(client, request, EMPTY_CALLBACK);
   }

}
