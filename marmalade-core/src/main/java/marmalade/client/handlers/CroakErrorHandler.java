package marmalade.client.handlers;

import marmalade.client.Response;

/**
 * Implementation of {@link ErrorHandler} that croaks
 * {@link ErrorResponseException}s.
 * 
 */
public class CroakErrorHandler implements ErrorHandler
{

   @Override
   public void onError(Response response)
   {
      throw new ErrorResponseException(response);
   }

}
