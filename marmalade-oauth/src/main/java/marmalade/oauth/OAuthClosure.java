package marmalade.oauth;

import marmalade.Marmalade;
import marmalade.MarmaladeException;
import marmalade.func.Closures.Closure;
import marmalade.oauth.spi.OAuthCredentialsProvider;
import marmalade.oauth.util.OAuthClientHelper;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.HttpRequestBase;

public class OAuthClosure implements Closure<HttpRequestBase>
{

   public static Marmalade Signed(final Marmalade ma)
   {
      return Signed(ma, null, null);
   }

   public static Marmalade Signed(final Marmalade ma, final OAuthCredentialsProvider provider)
   {
      return Signed(ma, provider, null);
   }

   public static Marmalade Signed(final Marmalade ma, final OAuthCredentialsProvider provider, final Object context)
   {
      return ma.decorate(new OAuthClosure(provider, context));
   }

   public static Marmalade Signed(final Marmalade ma, final Object context)
   {
      return Signed(ma, null, context);
   }

   private final Object context;
   private final OAuthCredentialsProvider provider;

   public OAuthClosure(final OAuthCredentialsProvider provider, final Object context)
   {
      this.context = context;
      this.provider = provider;
   }

   @Override
   public void on(final HttpRequestBase request)
   {
      try {
         OAuthClientHelper.signRequest(provider, context, request);
      } catch (AuthenticationException e) {
         throw new MarmaladeException(e);
      }
   }

}
