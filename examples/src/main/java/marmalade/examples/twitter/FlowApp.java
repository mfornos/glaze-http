package marmalade.examples.twitter;

import java.io.IOException;

import oauth.signpost.exception.OAuthException;

/**
 * Use this app to obtain OAuth authentication tokens.
 * 
 * <pre>
 * 1. Execute this app
 * 2. Copy the auth url in a browser
 * 3. Get the PIN code
 * 4. Paste it in the terminal
 * 5. Copy your tokens in 'twitter.properties'
 * </pre>
 * 
 */
public class FlowApp
{
   public static void main(String[] args) throws IOException, OAuthException
   {
      App app = new App();
      app.flow();

      System.exit(0);
   }
}
