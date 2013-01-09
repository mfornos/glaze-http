package glaze.oauth.util;

import glaze.client.Client;
import glaze.oauth.OAuthSchemeFactory;
import glaze.oauth.PreemptiveAuthorizer;
import glaze.oauth.creds.OAuthCredentials;
import glaze.oauth.spi.OAuthCredentialsProvider;
import glaze.spi.Registry;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthenticationException;

import com.google.common.base.Preconditions;

public class OAuthClientHelper
{

   public static void enablePreemptiveAuth(final Client client)
   {
      enablePreemptiveAuth(client, Registry.lookup(OAuthCredentialsProvider.class));
   }

   public static void enablePreemptiveAuth(final Client client, final OAuthCredentialsProvider credentialsProvider)
   {
      client.registerAuthScheme(OAuthSchemeFactory.SCHEME_NAME, new OAuthSchemeFactory());
      credentialsProvider.visit(client);
      client.proxyAuthPref("oauth", "basic", "digest");
      client.interceptRequest(new PreemptiveAuthorizer(), 0);
   }

   public static Header signRequest(final HttpRequest request) throws AuthenticationException
   {
      return signRequest(null, request);
   }

   public static Header signRequest(OAuthCredentialsProvider provider, final HttpRequest request)
         throws AuthenticationException
   {
      return signRequest(provider, null, request);
   }

   /**
    * Convenience method for per-request based authorization.
    * 
    * @param provider
    * @param context
    * @param request
    * @return
    * @throws AuthenticationException
    */
   public static Header signRequest(final OAuthCredentialsProvider provider, final Object context,
         final HttpRequest request) throws AuthenticationException
   {
      Preconditions.checkNotNull(request);

      OAuthCredentialsProvider oprov = provider == null ? Registry.lookup(OAuthCredentialsProvider.class) : provider;

      try {

         OAuthCredentials credentials = oprov.getOAuthCredentials(context);
         credentials.sign(request);

         return request.getFirstHeader(HttpHeaders.AUTHORIZATION);

      } catch (Exception e) {
         throw new AuthenticationException(e.getMessage(), e);
      }
   }

}
