package marmalade.client.handlers;

import org.apache.http.HttpResponse;

/**
 * Implementation of {@link ErrorHandler} that croaks
 * {@link ErrorResponseException}s.
 * 
 */
public class CroakErrorHandler implements ErrorHandler
{

   @Override
   public void onError(HttpResponse response)
   {
      throw new ErrorResponseException(response);
   }

}
