package marmalade.oauth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import marmalade.test.TrelloConfig;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

public class TrelloFlow
{
   public static void main(String[] args) throws Exception
   {
      
      // https://trello.com/1/authorize?key=substitutewithyourapplicationkey&name=My+Application&expiration=1day&response_type=token&scope=read,write
      
      OAuthConsumer consumer = new DefaultOAuthConsumer(TrelloConfig.getKey(), TrelloConfig.getSecret());

      OAuthProvider provider = new DefaultOAuthProvider("https://trello.com/1/OAuthGetRequestToken", "https://trello.com/1/OAuthGetAccessToken", "https://trello.com/1/OAuthAuthorizeToken?name=CucuApp&scope=read,write");
      
      System.out.println("Fetching request token from Trello...");

      // we do not support callbacks, thus pass OOB
      String authUrl = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);

      System.out.println("Request token: " + consumer.getToken());
      System.out.println("Token secret: " + consumer.getTokenSecret());

      System.out.println("Now visit:\n" + authUrl + "\n... and grant this app authorization");
      System.out.println("Enter the PIN (verify token) code and hit ENTER when you're done:");

      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      String pin = br.readLine();

      System.out.println("Fetching access token from Trello...");

      provider.retrieveAccessToken(consumer, pin);

      System.out.println("Access token: " + consumer.getToken());
      System.out.println("Token secret: " + consumer.getTokenSecret());

      // Test consumer
      URL url = new URL("https://api.trello.com/1/members/me/boards");
      HttpURLConnection request = (HttpURLConnection) url.openConnection();

      consumer.sign(request);

      System.out.println("Sending request to Trello...");
      request.connect();

      System.out.println("Response: " + request.getResponseCode() + " " + request.getResponseMessage());
   
   }
}
