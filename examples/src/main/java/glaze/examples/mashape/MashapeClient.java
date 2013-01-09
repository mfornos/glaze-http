package glaze.examples.mashape;

import java.io.IOException;

import glaze.client.async.DefaultAsyncClient;
import glaze.client.config.DefaultPropertyConfig;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

public class MashapeClient extends DefaultAsyncClient
{
   private class MashapeAuth implements HttpRequestInterceptor
   {
      private final String publicKey;
      private final String privateKey;

      public MashapeAuth(DefaultPropertyConfig config)
      {
         this.publicKey = config.get("key.public");
         this.privateKey = config.get("key.private");
      }

      @Override
      public void process(HttpRequest req, HttpContext ctx) throws HttpException, IOException
      {
         req.addHeader("X-Mashape-Authorization", AuthUtil.getAuthToken(publicKey, privateKey));
      }

   }

   public MashapeClient()
   {
      super();
      DefaultPropertyConfig config = new DefaultPropertyConfig("mashape.config", "mashape.properties");
      interceptRequest(new MashapeAuth(config), 0);
   }
}
