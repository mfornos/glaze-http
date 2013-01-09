package glaze.test;

import glaze.oauth.creds.DefaultCredentialsProvider;

public class FixedCredentialsProvider extends DefaultCredentialsProvider
{

   public FixedCredentialsProvider()
   {
      super("keyone", "secretone", "abcdefg", "secret");
   }

}
