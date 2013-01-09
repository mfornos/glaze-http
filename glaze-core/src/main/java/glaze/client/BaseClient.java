package glaze.client;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.BasicHttpContext;

public abstract class BaseClient implements Client
{
   // TODO lazy creation
   protected AuthCache authCache;

   protected CredentialsProvider credentialsProvider;

   protected CookieStore cookieStore;

   public BaseClient()
   {
      this.authCache = new BasicAuthCache();
      this.credentialsProvider = new BasicCredentialsProvider();
      this.cookieStore = new BasicCookieStore();
   }

   @Override
   public Client auth(final AuthScope authScope, final Credentials creds)
   {
      this.credentialsProvider.setCredentials(authScope, creds);
      return this;
   }

   @Override
   public Client auth(final Credentials cred)
   {
      return auth(AuthScope.ANY, cred);
   }

   @Override
   public Client auth(final HttpHost host, final Credentials creds)
   {
      AuthScope authScope = host != null ? new AuthScope(host) : AuthScope.ANY;
      return auth(authScope, creds);
   }

   @Override
   public Client auth(final HttpHost host, final String username, final String password)
   {
      return auth(host, new UsernamePasswordCredentials(username, password));
   }

   @Override
   public Client auth(final HttpHost host, final String username, final String password, final String workstation,
         final String domain)
   {
      return auth(host, new NTCredentials(username, password, workstation, domain));
   }

   @Override
   public Client auth(final String username, final String password)
   {
      return auth(new UsernamePasswordCredentials(username, password));
   }

   @Override
   public Client auth(final String username, final String password, final String workstation, final String domain)
   {
      return auth(new NTCredentials(username, password, workstation, domain));
   }

   @Override
   public Client authPreemptive(final HttpHost host)
   {
      this.authCache.put(host, new BasicScheme(ChallengeState.TARGET));
      return this;
   }

   @Override
   public Client authPreemptiveProxy(final HttpHost host)
   {
      this.authCache.put(host, new BasicScheme(ChallengeState.PROXY));
      return this;
   }

   @Override
   public Client clearAuth()
   {
      if (this.credentialsProvider != null) {
         this.credentialsProvider.clear();
      }
      return this;
   }

   @Override
   public Client clearCookies()
   {
      if (this.cookieStore != null) {
         this.cookieStore.clear();
      }
      return this;
   }

   @Override
   public Client cookieStore(final CookieStore cookieStore)
   {
      this.cookieStore = cookieStore;
      return this;
   }

   protected BasicHttpContext prepareLocalContext()
   {
      BasicHttpContext localContext = new BasicHttpContext();
      localContext.setAttribute(ClientContext.CREDS_PROVIDER, this.credentialsProvider);
      localContext.setAttribute(ClientContext.AUTH_CACHE, this.authCache);
      localContext.setAttribute(ClientContext.COOKIE_STORE, this.cookieStore);
      return localContext;
   }

}
