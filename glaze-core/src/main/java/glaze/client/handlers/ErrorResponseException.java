package glaze.client.handlers;

import glaze.GlazeException;
import glaze.client.Response;
import glaze.client.wire.tasks.SerializableResponse;

/**
 * Exception for HTTP error conditions.
 * 
 */
public class ErrorResponseException extends GlazeException
{

   private final SerializableResponse response;

   private static final long serialVersionUID = -1447820840370028597L;

   public ErrorResponseException()
   {
      this("");
   }

   public ErrorResponseException(Response response)
   {
      this(response.statusLine().toString(), response);
   }

   public ErrorResponseException(String message)
   {
      this(message, null, null);
   }

   public ErrorResponseException(String message, Response response)
   {
      this(message, null, response);
   }

   public ErrorResponseException(String message, Throwable cause)
   {
      this(message, cause, null);
   }

   public ErrorResponseException(String message, Throwable cause, Response response)
   {
      super(message, cause);

      if (response == null) {
         Throwable found = findErrorResponse(cause);
         this.response = found == null ? null : ((ErrorResponseException) found).getResponse();
      } else {
         this.response = response == null ? null : new SerializableResponse(response);
      }
   }

   public ErrorResponseException(Throwable cause)
   {
      this("", cause, null);
   }

   public SerializableResponse getResponse()
   {
      return response;
   }

   public int getStatusCode()
   {
      return response == null ? -1 : response.status();
   }

   private Throwable findErrorResponse(Throwable cause)
   {
      if (cause != null && ErrorResponseException.class.isAssignableFrom(cause.getClass())) {
         return cause;
      } else if (cause.getCause() != null) {
         return findErrorResponse(cause.getCause());
      }

      return null;
   }

}
