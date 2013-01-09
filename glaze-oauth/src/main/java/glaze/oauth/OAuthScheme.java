package glaze.oauth;

import glaze.oauth.creds.OAuthCredentials;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.impl.auth.RFC2617Scheme;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.protocol.HttpContext;

@NotThreadSafe
class OAuthScheme extends RFC2617Scheme
{

   private final String defaultRealm;

   // Whether the authentication process is complete (for the current context)
   private boolean complete;

   OAuthScheme(String defaultRealm)
   {
      this.defaultRealm = defaultRealm;
   }

   @Override
   @Deprecated
   public Header authenticate(Credentials paramCredentials, HttpRequest paramHttpRequest)
         throws AuthenticationException
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context)
         throws AuthenticationException
   {
      try {

         HttpRequest original = ((RequestWrapper) request).getOriginal();
         ((OAuthCredentials) credentials).sign(original);

         return original.getFirstHeader("Authorization");

      } catch (Exception e) {
         throw new AuthenticationException(e.getMessage(), e);
      }
   }

   @Override
   public String getRealm()
   {
      String realm = super.getRealm();
      if (realm == null) {
         realm = defaultRealm;
      }
      return realm;
   }

   @Override
   public String getSchemeName()
   {
      return OAuthSchemeFactory.SCHEME_NAME;
   }

   @Override
   public boolean isComplete()
   {
      return complete;
   }

   @Override
   public boolean isConnectionBased()
   {
      return false;
   }

   @Override
   public void processChallenge(Header challenge) throws MalformedChallengeException
   {
      super.processChallenge(challenge);
      complete = true;
   }

}
