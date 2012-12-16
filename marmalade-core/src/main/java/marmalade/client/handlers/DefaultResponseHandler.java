package marmalade.client.handlers;

import java.io.IOException;

import marmalade.client.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

public class DefaultResponseHandler implements ResponseHandler<Response>
{

   private final ErrorHandler errorHandler;

   public DefaultResponseHandler(ErrorHandler errorHandler)
   {
      this.errorHandler = errorHandler;
   }

   @Override
   public Response handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException
   {
      Response response = new Response(httpResponse);
      if (response.isError()) {
         try {
            // TODO signal entity consumption on return?
            errorHandler.onError(httpResponse);
            return null;
         } finally {
            EntityUtils.consumeQuietly(httpResponse.getEntity());
         }
      }
      return response;
   }

}
