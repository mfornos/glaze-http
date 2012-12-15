package marmalade.client.handlers;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import marmalade.MarmaladeException;

/**
 * Exception for HTTP error conditions.
 * 
 */
public class ErrorResponseException extends MarmaladeException
{

   private final HttpResponse response;
   private final StatusLine statusLine;
   private final int statusCode;

   private static final long serialVersionUID = -1447820840370028597L;

   public ErrorResponseException(HttpResponse response)
   {
      super(response.getStatusLine().toString());
      this.response = response;
      this.statusLine = response.getStatusLine();
      this.statusCode = statusLine.getStatusCode();
   }

   public HttpResponse getResponse()
   {
      return response;
   }

   public int getStatusCode()
   {
      return statusCode;
   }

   public StatusLine getStatusLine()
   {
      return statusLine;
   }

}
