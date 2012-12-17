package marmalade.client.handlers;

import marmalade.client.Response;

/**
 * Handles error conditions on HTTP interactions.
 * 
 */
public interface ErrorHandler
{

   void onError(Response response);

}
