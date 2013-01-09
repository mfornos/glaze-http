package glaze.client.handlers;

import glaze.client.Response;

import java.io.IOException;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

public abstract class DefaultResponseHandler implements ResponseHandler<Response>
{

   public DefaultResponseHandler()
   {

   }

   @Override
   public Response handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException
   {
      Response response = new Response(httpResponse);
      if (response.isError()) {
         try {
            return onError(response);
         } finally {
            response.discardContent();
         }
      }
      return onResponse(response);
   }

   abstract protected Response onResponse(Response response);

   abstract protected Response onError(Response response);
}
