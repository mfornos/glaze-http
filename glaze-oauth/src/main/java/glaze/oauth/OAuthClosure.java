package glaze.oauth;

import glaze.Glaze;
import glaze.GlazeException;
import glaze.func.Closures.Closure;
import glaze.oauth.spi.OAuthCredentialsProvider;
import glaze.oauth.util.OAuthClientHelper;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.HttpRequestBase;

public class OAuthClosure implements Closure<HttpRequestBase>
{

   public static Glaze Signed(final Glaze ma)
   {
      return Signed(ma, null, null);
   }

   public static Glaze Signed(final Glaze ma, final OAuthCredentialsProvider provider)
   {
      return Signed(ma, provider, null);
   }

   public static Glaze Signed(final Glaze ma, final OAuthCredentialsProvider provider, final Object context)
   {
      return ma.decorate(new OAuthClosure(provider, context));
   }

   public static Glaze Signed(final Glaze ma, final Object context)
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
         throw new GlazeException(e);
      }
   }

}
