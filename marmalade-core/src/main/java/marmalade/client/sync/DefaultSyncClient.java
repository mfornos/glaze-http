package marmalade.client.sync;

import java.io.IOException;
import java.util.Arrays;

import marmalade.MarmaladeException;
import marmalade.client.BaseClient;
import marmalade.client.Client;
import marmalade.client.Response;
import marmalade.client.handlers.ErrorHandler;
import marmalade.client.handlers.ErrorResponseHandler;
import marmalade.client.handlers.MapperResponseHandler;
import marmalade.client.interceptors.PreemptiveAuthorizer;
import marmalade.spi.Registry;

import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.params.HttpConnectionParamBean;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParamBean;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.VersionInfo;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link SyncClient}.
 * 
 */
public class DefaultSyncClient extends BaseClient implements SyncClient
{

   private static HttpClient createDefaultHttpClient()
   {
      HttpClient httpClient;

      if (Registry.isRegitered(HttpClient.class)) {

         httpClient = Registry.lookup(HttpClient.class);

      } else {
         SchemeRegistry schreg = SchemeRegistryFactory.createSystemDefault();
         PoolingClientConnectionManager pool = new PoolingClientConnectionManager(schreg);
         pool.setDefaultMaxPerRoute(100);
         pool.setMaxTotal(200);

         DefaultHttpClient hc = new DefaultHttpClient(pool);
         HttpParams params = hc.getParams();
         HttpConnectionParamBean connBean = new HttpConnectionParamBean(params);
         connBean.setConnectionTimeout(300000);
         HttpProtocolParamBean protocolBean = new HttpProtocolParamBean(params);
         VersionInfo versionInfo = VersionInfo.loadVersionInfo("marmalade", DefaultSyncClient.class.getClassLoader());
         protocolBean.setUserAgent(String.format("Marmalade-HttpClient/%s", versionInfo));
         httpClient = hc;
      }

      return httpClient;
   }

   private final HttpClient httpClient;

   public DefaultSyncClient()
   {
      this(createDefaultHttpClient());
   }

   public DefaultSyncClient(HttpClient httpClient)
   {
      super();
      this.httpClient = httpClient;
   }

   @Override
   public Client authPreemptive(String schemeName)
   {
      DefaultHttpClient httpClient = (DefaultHttpClient) getHttpClient();
      httpClient.addRequestInterceptor(new PreemptiveAuthorizer(schemeName), 0);
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.SyncClient#execute(org.apache.http.client.methods.
    * HttpUriRequest )
    */
   @Override
   public Response execute(HttpUriRequest request)
   {
      try {
         return new Response(httpClient.execute(request, prepareLocalContext()));
      } catch (IOException e) {
         request.abort();
         throw new MarmaladeException(e);
      }
   }

   @Override
   public Response execute(HttpUriRequest request, ErrorHandler errorHandler)
   {
      return execute(request, errorHandler, prepareLocalContext());
   }

   @Override
   public Response execute(HttpUriRequest request, ErrorHandler errorHandler, HttpContext context)
   {
      return errorHandler == null ? execute(request, context)
            : execute(request, new ErrorResponseHandler(errorHandler), context);

   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.SyncClient#execute(org.apache.http.client.methods.
    * HttpUriRequest , org.apache.http.protocol.HttpContext)
    */
   @Override
   public Response execute(HttpUriRequest request, HttpContext context)
   {
      try {
         return new Response(httpClient.execute(request, context));
      } catch (IOException e) {
         request.abort();
         throw new MarmaladeException(e);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.SyncClient#execute(org.apache.http.client.methods.
    * HttpUriRequest , org.apache.http.client.ResponseHandler)
    */
   @Override
   public <T> T execute(HttpUriRequest request, ResponseHandler<T> handler)
   {
      try {
         return httpClient.execute(request, handler, prepareLocalContext());
      } catch (IOException e) {
         request.abort();
         throw new MarmaladeException(e);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.SyncClient#execute(org.apache.http.client.methods.
    * HttpUriRequest , org.apache.http.client.ResponseHandler,
    * org.apache.http.protocol.HttpContext)
    */
   @Override
   public <T> T execute(HttpUriRequest request, ResponseHandler<T> handler, HttpContext context)
   {
      try {
         return httpClient.execute(request, handler, context);
      } catch (IOException e) {
         request.abort();
         throw new MarmaladeException(e);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.SyncClient#getHttpClient()
    */
   @Override
   public HttpClient getHttpClient()
   {
      return httpClient;
   }

   @Override
   public <T> T map(HttpUriRequest request, Class<T> type)
   {
      return map(Registry.NS_DEFAULT, request, type);
   }

   @Override
   public <T> T map(HttpUriRequest request, Class<T> type, ContentType forceType)
   {
      return map(Registry.NS_DEFAULT, request, type, forceType);
   }

   @Override
   public <T> T map(HttpUriRequest request, Class<T> type, ErrorHandler errorHandler)
   {
      return map(Registry.NS_DEFAULT, request, type, errorHandler);
   }

   @Override
   public <T> T map(HttpUriRequest request, HttpContext context, Class<T> type)
   {
      return map(Registry.NS_DEFAULT, request, context, type);
   }

   @Override
   public <T> T map(HttpUriRequest request, HttpContext context, Class<T> type, ContentType forceType)
   {
      return map(Registry.NS_DEFAULT, request, context, type, forceType);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * marmalade.client.SyncClient#map(org.apache.http.client.methods.HttpUriRequest
    * , java.lang.Class)
    */
   @Override
   public <T> T map(String namespace, HttpUriRequest request, Class<T> type)
   {
      return execute(request, new MapperResponseHandler<T>(namespace, type));
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * marmalade.client.SyncClient#map(org.apache.http.client.methods.HttpUriRequest
    * , java.lang.Class, org.apache.http.entity.ContentType)
    */
   @Override
   public <T> T map(String namespace, HttpUriRequest request, Class<T> type, ContentType forceType)
   {
      return execute(request, new MapperResponseHandler<T>(namespace, type, forceType));
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.sync.SyncClient#map(org.apache.http.client.methods.
    * HttpUriRequest, java.lang.Class, marmalade.client.handlers.ErrorHandler)
    */
   @Override
   public <T> T map(String namespace, HttpUriRequest request, Class<T> type, ErrorHandler errorHandler)
   {
      return execute(request, new MapperResponseHandler<T>(namespace, type, errorHandler));
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * marmalade.client.SyncClient#map(org.apache.http.client.methods.HttpUriRequest
    * , org.apache.http.protocol.HttpContext, java.lang.Class)
    */
   @Override
   public <T> T map(String namespace, HttpUriRequest request, HttpContext context, Class<T> type)
   {
      return execute(request, new MapperResponseHandler<T>(namespace, type), context);
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * marmalade.client.SyncClient#map(org.apache.http.client.methods.HttpUriRequest
    * , org.apache.http.protocol.HttpContext, java.lang.Class,
    * org.apache.http.entity.ContentType)
    */
   @Override
   public <T> T map(String namespace, HttpUriRequest request, HttpContext context, Class<T> type, ContentType forceType)
   {
      return execute(request, new MapperResponseHandler<T>(type, forceType), context);
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.SyncClient#proxyAuthPref(java.lang.String)
    */
   @Override
   public void proxyAuthPref(String... authpref)
   {
      Preconditions.checkNotNull(authpref, "Please, specify a valid auth policy chain.");
      httpClient.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, Arrays.asList(authpref));
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * marmalade.client.SyncClient#registerScheme(org.apache.http.conn.scheme
    * .Scheme )
    */
   @Override
   public void registerScheme(final Scheme scheme)
   {
      httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.SyncClient#shutdown()
    */
   @Override
   public void shutdown()
   {
      httpClient.getConnectionManager().shutdown();
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.SyncClient#trustSelfSignedCertificates()
    */
   @Override
   public void trustSelfSignedCertificates()
   {
      try {
         SSLSocketFactory sslsf = new SSLSocketFactory(new TrustSelfSignedStrategy(), SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
         registerScheme(new Scheme("https", 443, sslsf));
      } catch (Exception e) {
         throw new MarmaladeException(e);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see marmalade.client.SyncClient#unregisterScheme(java.lang.String)
    */
   @Override
   public void unregisterScheme(final String name)
   {
      httpClient.getConnectionManager().getSchemeRegistry().unregister(name);
   }

}
