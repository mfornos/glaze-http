package marmalade.client.handlers;

import marmalade.MarmaladeException;
import marmalade.client.Response;
import marmalade.client.wire.tasks.SerializableResponse;

/**
 * Exception for HTTP error conditions.
 * 
 */
public class ErrorResponseException extends MarmaladeException
{

   private final SerializableResponse response;

   private static final long serialVersionUID = -1447820840370028597L;

   public ErrorResponseException(Response response)
   {
      super(response.statusLine().toString());
      this.response = new SerializableResponse(response);
   }

   public SerializableResponse getResponse()
   {
      return response;
   }

   public int getStatusCode()
   {
      return response.status();
   }

}
