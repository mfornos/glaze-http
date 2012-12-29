package marmalade.test;

import marmalade.oauth.creds.DefaultCredentialsProvider;

public class FixedCredentialsProvider extends DefaultCredentialsProvider
{

   public FixedCredentialsProvider()
   {
      super("keyone", "secretone", "abcdefg", "secret");
   }

}
