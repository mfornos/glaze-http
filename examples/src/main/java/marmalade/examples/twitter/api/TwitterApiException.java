package marmalade.examples.twitter.api;

import marmalade.client.handlers.ErrorResponseException;

public class TwitterApiException extends ErrorResponseException
{

   private static final long serialVersionUID = -6841428256784789393L;

   public TwitterApiException(Throwable cause)
   {
      super(cause);
   }

}
