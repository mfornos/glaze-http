package marmalade.client.handlers;

import marmalade.client.Response;

public class ErrorResponseHandler extends DefaultResponseHandler
{
   private final ErrorHandler errorHandler;
   
   public ErrorResponseHandler(ErrorHandler errorHandler)
   {
      this.errorHandler = errorHandler;
   }

   @Override
   protected Response onResponse(Response response)
   {
      return response;
   }

   @Override
   protected Response onError(Response response)
   {
      errorHandler.onError(response);
      return null;
   }
   
   

}
