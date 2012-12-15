package marmalade.client.handlers;

import org.apache.http.HttpResponse;

/**
 * Handles error conditions on HTTP interactions.
 * 
 */
public interface ErrorHandler
{

   void onError(HttpResponse response);

}
