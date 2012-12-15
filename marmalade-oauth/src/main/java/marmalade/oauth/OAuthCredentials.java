package marmalade.oauth;

import java.security.Principal;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpRequest;
import org.apache.http.auth.Credentials;

/**
 *
 */
public class OAuthCredentials implements Credentials
{

   private final OAuthConsumer consumer;

   public OAuthCredentials(OAuthConsumer consumer)
   {
      this.consumer = consumer;
   }

   @Override
   public String getPassword()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public Principal getUserPrincipal()
   {
      throw new UnsupportedOperationException();
   }

   public void sign(HttpRequest request) throws OAuthMessageSignerException, OAuthExpectationFailedException,
         OAuthCommunicationException
   {
      consumer.sign(request);
   }

}
