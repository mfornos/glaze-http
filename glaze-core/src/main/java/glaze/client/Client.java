package glaze.client;

import glaze.spi.Dispose;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CookieStore;

/**
 * Common interface for Marmalade clients.
 * 
 */
public interface Client
{

   /**
    * Configures the underlying BasicCredentialsProvider.
    * 
    * @param authScope
    *           The authorization scope
    * @param creds
    *           The credentials
    * @return this client instance
    */
   Client auth(final AuthScope authScope, final Credentials creds);

   /**
    * Configures the underlying BasicCredentialsProvider.
    * 
    * @param cred
    *           The credentials
    * @return this client instance
    */
   Client auth(final Credentials cred);

   /**
    * Configures the underlying BasicCredentialsProvider.
    * 
    * @param host
    *           Target host
    * @param creds
    *           The credentials
    * @return this client instance
    */
   Client auth(final HttpHost host, final Credentials creds);

   /**
    * Configures the underlying BasicCredentialsProvider.
    * 
    * @param host
    *           Target host
    * @param username
    *           The user name
    * @param password
    *           The password
    * @return this client instance
    */
   Client auth(final HttpHost host, final String username, final String password);

   /**
    * Configures the underlying BasicCredentialsProvider.
    * 
    * @param host
    *           Target host
    * @param username
    *           The user name
    * @param password
    *           The password
    * @param workstation
    * @param domain
    * @return this client instance
    */
   Client auth(final HttpHost host, final String username, final String password, final String workstation,
         final String domain);

   /**
    * Configures the underlying BasicCredentialsProvider.
    * 
    * @param username
    *           The user name
    * @param password
    *           The password
    * @return this client instance
    */
   Client auth(final String username, final String password);

   /**
    * Configures the underlying BasicCredentialsProvider.
    * 
    * @param username
    *           The user name
    * @param password
    *           The password
    * @param workstation
    * @param domain
    * @return this client instance
    */
   Client auth(final String username, final String password, final String workstation, final String domain);

   /**
    * Configures the underlying AuthCache for {@link ChallengeState.TARGET}.
    * 
    * @param host
    *           Target host
    * @return this client instance
    */
   Client authPreemptive(final HttpHost host);

   /**
    * Configures the underlying AuthCache.
    * 
    * @param schemeName
    *           The schema name
    * @return this client instance
    */
   Client authPreemptive(final String schemeName);

   /**
    * Configures the underlying AuthCache for {@link ChallengeState.PROXY}.
    * 
    * @param host
    *           Target host
    * @return this client instance
    */
   Client authPreemptiveProxy(final HttpHost host);

   /**
    * Specifies the authorization chain.
    * 
    * @param authpref
    *           An ordered list of authorization schemes
    */
   void proxyAuthPref(String... authpref);

   /**
    * Clears the underlying CredentialsProvider.
    * 
    * @return this client instance
    */
   Client clearAuth();

   /**
    * Clears the cookie store.
    * 
    * @return this client instance
    */
   Client clearCookies();

   /**
    * Sets a cookie store.
    * 
    * @param cookieStore
    *           The cookie store
    * @return this client instance
    */
   Client cookieStore(final CookieStore cookieStore);

   /**
    * Adds a request interceptor.
    * 
    * @param interceptor
    *           The request interceptor
    * @return this client instance
    */
   Client interceptRequest(HttpRequestInterceptor interceptor);

   /**
    * Adds a request interceptor at the given position.
    * 
    * @param interceptor
    *           The request interceptor
    * @param position
    *           The position in the interceptors chain
    * @return this client instance
    */
   Client interceptRequest(HttpRequestInterceptor interceptor, int position);

   /**
    * Adds a response interceptor.
    * 
    * @param interceptor
    *           The response interceptor
    * @return this client instance
    */
   Client interceptResponse(HttpResponseInterceptor interceptor);

   /**
    * Adds a response interceptor at the given position.
    * 
    * @param interceptor
    *           The response interceptor
    * @param position
    *           The position in the interceptors chain
    * @return this client instance
    */
   Client interceptResponse(HttpResponseInterceptor interceptor, int position);

   /**
    * Shutdowns the underlying connection manager.
    */
   @Dispose
   void shutdown();

   /**
    * Registers an authentication scheme.
    * 
    * @param schemeName
    *           The scheme name
    * @param schemeFactory
    *           The scheme factory
    */
   void registerAuthScheme(String schemeName, AuthSchemeFactory schemeFactory);

}
