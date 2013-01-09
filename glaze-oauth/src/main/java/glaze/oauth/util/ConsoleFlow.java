package glaze.oauth.util;

import glaze.oauth.OAuthFlow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthException;

public class ConsoleFlow
{
   public static void execute(OAuthFlow flow) throws OAuthException, IOException
   {
      String authUrl = flow.requestAuthorization();
      OAuthConsumer consumer = flow.getConsumer();

      System.out.format("Fetching request token from %s...\n", flow.name());
      System.out.format("Request token: %s\n", consumer.getToken());
      System.out.format("Token secret: %s\n", consumer.getTokenSecret());
      System.out.format("Now visit:\n%s\n... and grant this app authorization\n", authUrl);
      System.out.println("Enter the PIN (verify token) code and hit ENTER when you're done:");

      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      String pin = br.readLine();

      System.out.format("Fetching access token from %s...\n", flow.name());

      // Same instance
      OAuthConsumer c = flow.confirmAuthorization(pin);

      System.out.format("Access token: '%s'\n", c.getToken());
      System.out.format("Token secret: '%s'\n", c.getTokenSecret());
   }
}
