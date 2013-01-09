package glaze.client.wire.tasks;

import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendCall extends CallableRequest<SerializableResponse>
{
   private static final long serialVersionUID = 1141382162460854194L;

   private static final Logger LOGGER = LoggerFactory.getLogger(SendCall.class);

   private static final SerializableResponseCallback<SerializableResponse> EMPTY_CALLBACK = new SerializableResponseCallback<SerializableResponse>()
   {
      private static final long serialVersionUID = -9009263626696512179L;

      @Override
      public SerializableResponse onException(Exception e)
      {
         LOGGER.error(e.getMessage(), e);
         return null;
      }

      @Override
      public SerializableResponse onResponse(SerializableResponse response)
      {
         if (LOGGER.isDebugEnabled())
            LOGGER.debug("Provide a callback and do something interesting with {}", response);
         return response;
      }
   };

   public SendCall(HttpUriRequest request)
   {
      super(request, EMPTY_CALLBACK);
   }

   public SendCall(HttpUriRequest request, SerializableResponseCallback<SerializableResponse> callback)
   {
      super(request, callback);
   }

}
