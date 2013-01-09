package glaze.client.handlers;

import glaze.client.Response;

/**
 * Implementation of {@link ErrorHandler} that croaks
 * {@link ErrorResponseException}s.
 * 
 */
public class CroakErrorHandler implements ErrorHandler
{

   private static CroakErrorHandler instance;

   public static synchronized ErrorHandler instance()
   {
      if (instance == null) {
         instance = new CroakErrorHandler();
      }
      return instance;
   }

   @Override
   public void onError(Response response)
   {
      throw new ErrorResponseException(response);
   }

}
