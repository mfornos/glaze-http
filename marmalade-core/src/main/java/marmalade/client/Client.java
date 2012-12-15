package marmalade.client;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CookieStore;

public interface Client
{

   public abstract Client auth(final AuthScope authScope, final Credentials creds);

   public abstract Client auth(final Credentials cred);

   public abstract Client auth(final HttpHost host, final Credentials creds);

   public abstract Client auth(final HttpHost host, final String username, final String password);

   public abstract Client auth(final HttpHost host, final String username, final String password,
         final String workstation, final String domain);

   public abstract Client auth(final String username, final String password);

   public abstract Client auth(final String username, final String password, final String workstation,
         final String domain);

   public abstract Client authPreemptive(final HttpHost host);

   public abstract Client authPreemptiveProxy(final HttpHost host);

   public abstract Client clearAuth();

   public abstract Client clearCookies();

   public abstract Client cookieStore(final CookieStore cookieStore);

   /**
    * Shutdowns the underlying connection manager.
    */
   void shutdown();

}
