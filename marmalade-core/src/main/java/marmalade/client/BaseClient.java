package marmalade.client;

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

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.Client#auth(org.apache.http.auth.AuthScope,
    * org.apache.http.auth.Credentials)
    */
   @Override
   public Client auth(final AuthScope authScope, final Credentials creds)
   {
      this.credentialsProvider.setCredentials(authScope, creds);
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.Client#auth(org.apache.http.auth.Credentials)
    */
   @Override
   public Client auth(final Credentials cred)
   {
      return auth(AuthScope.ANY, cred);
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.Client#auth(org.apache.http.HttpHost,
    * org.apache.http.auth.Credentials)
    */
   @Override
   public Client auth(final HttpHost host, final Credentials creds)
   {
      AuthScope authScope = host != null ? new AuthScope(host) : AuthScope.ANY;
      return auth(authScope, creds);
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.Client#auth(org.apache.http.HttpHost,
    * java.lang.String, java.lang.String)
    */
   @Override
   public Client auth(final HttpHost host, final String username, final String password)
   {
      return auth(host, new UsernamePasswordCredentials(username, password));
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.Client#auth(org.apache.http.HttpHost,
    * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public Client auth(final HttpHost host, final String username, final String password, final String workstation,
         final String domain)
   {
      return auth(host, new NTCredentials(username, password, workstation, domain));
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.Client#auth(java.lang.String, java.lang.String)
    */
   @Override
   public Client auth(final String username, final String password)
   {
      return auth(new UsernamePasswordCredentials(username, password));
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.Client#auth(java.lang.String, java.lang.String,
    * java.lang.String, java.lang.String)
    */
   @Override
   public Client auth(final String username, final String password, final String workstation, final String domain)
   {
      return auth(new NTCredentials(username, password, workstation, domain));
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.Client#authPreemptive(org.apache.http.HttpHost)
    */
   @Override
   public Client authPreemptive(final HttpHost host)
   {
      this.authCache.put(host, new BasicScheme(ChallengeState.TARGET));
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.Client#authPreemptiveProxy(org.apache.http.HttpHost)
    */
   @Override
   public Client authPreemptiveProxy(final HttpHost host)
   {
      this.authCache.put(host, new BasicScheme(ChallengeState.PROXY));
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.Client#clearAuth()
    */
   @Override
   public Client clearAuth()
   {
      if (this.credentialsProvider != null) {
         this.credentialsProvider.clear();
      }
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.Client#clearCookies()
    */
   @Override
   public Client clearCookies()
   {
      if (this.cookieStore != null) {
         this.cookieStore.clear();
      }
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * marmalade.client.Client#cookieStore(org.apache.http.client.CookieStore)
    */
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
