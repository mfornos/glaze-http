package glaze.client.handlers;

import glaze.client.Response;

/**
 * Handles error conditions on HTTP interactions.
 * 
 */
public interface ErrorHandler
{

   void onError(Response response);

}
