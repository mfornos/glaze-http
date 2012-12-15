package marmalade.oauth.spi;

import marmalade.client.sync.SyncClient;
import marmalade.oauth.OAuthSchemeFactory;
import marmalade.oauth.PreemptiveAuthorizer;
import marmalade.spi.HookContrib;
import marmalade.spi.Registry;

import org.apache.http.impl.client.AbstractHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 */
public class OAuthHook implements HookContrib
{

   private static final Logger LOGGER = LoggerFactory.getLogger(OAuthHook.class);

   @Override
   public boolean acceptMapper(String mime)
   {
      return false;
   }

   @Override
   public boolean acceptService(Class<?> type)
   {
      return type.isAssignableFrom(SyncClient.class);
   }

   @Override
   public void visitMapper(String mime, ObjectMapper mapper)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void visitService(Class<?> type, Object impl)
   {
      LOGGER.info("Hooking SyncClient...");

      SyncClient client = (SyncClient) impl;
      AbstractHttpClient httpClient = (AbstractHttpClient) client.getHttpClient();
      httpClient.getAuthSchemes().register(OAuthSchemeFactory.SCHEME_NAME, new OAuthSchemeFactory());

      OAuthCredentialsProvider provider = Registry.lookup(OAuthCredentialsProvider.class);
      provider.visit(client);
      client.proxyAuthPref("oauth", "basic", "digest");

      // TODO challange based
      httpClient.addRequestInterceptor(new PreemptiveAuthorizer(), 0);

      LOGGER.info("Hooked OK"); // on a feeling...
   }

}
