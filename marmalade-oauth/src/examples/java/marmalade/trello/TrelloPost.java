package marmalade.trello;

import marmalade.Marmalade;
import marmalade.client.Response;
import marmalade.client.interceptors.DebugInterceptor;
import marmalade.client.sync.SyncClient;
import marmalade.trello.data.Card;
import marmalade.spi.Registry;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.impl.client.DefaultHttpClient;

public class TrelloPost
{

   public static void main(String[] args) throws OAuthMessageSignerException, OAuthExpectationFailedException,
         OAuthCommunicationException
   {
      new TrelloPost().simple();
   }

   public void simple() throws OAuthMessageSignerException, OAuthExpectationFailedException,
         OAuthCommunicationException
   {
      SyncClient client = Registry.lookup(SyncClient.class);
      ((DefaultHttpClient) client.getHttpClient()).addRequestInterceptor(new DebugInterceptor());
      ((DefaultHttpClient) client.getHttpClient()).addResponseInterceptor(new DebugInterceptor());

      // Response response =
      // Marmalade.Get("https://api.trello.com/1/boards/4e96e1e90441f70000453ffc/lists").send();

      Card card = new Card("Hello from Marmalade baby!", "Automatic message...", "4e96e2910441f7000045755f");
      Response response = Marmalade.Post("https://api.trello.com/1/cards").bean(card).send(client);

      //Assert.assertEquals(response.getStatus(), HttpStatus.SC_OK);

      System.out.println(response.getHttpResponse());
      System.out.println(response.asString());
   }
}
