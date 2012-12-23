package marmalade.client;

import marmalade.spi.Dispose;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CookieStore;

/**
 * Common interface for Marmalade clients.
 * 
 */
public interface Client
{

   Client auth(final AuthScope authScope, final Credentials creds);

   Client auth(final Credentials cred);

   Client auth(final HttpHost host, final Credentials creds);

   Client auth(final HttpHost host, final String username, final String password);

   Client auth(final HttpHost host, final String username, final String password, final String workstation,
         final String domain);

   Client auth(final String username, final String password);

   Client auth(final String username, final String password, final String workstation, final String domain);

   Client authPreemptive(final HttpHost host);

   Client authPreemptive(final String schemeName);

   Client authPreemptiveProxy(final HttpHost host);

   Client clearAuth();

   Client clearCookies();

   Client cookieStore(final CookieStore cookieStore);

   Client interceptRequest(HttpRequestInterceptor interceptor);

   Client interceptRequest(HttpRequestInterceptor interceptor, int position);

   Client interceptResponse(HttpResponseInterceptor interceptor);

   Client interceptResponse(HttpResponseInterceptor interceptor, int position);

   /**
    * Shutdowns the underlying connection manager.
    */
   @Dispose
   void shutdown();

}
