package marmalade.client.async;

import java.util.Arrays;
import java.util.concurrent.Future;

import marmalade.MarmaladeException;
import marmalade.client.BaseClient;
import marmalade.client.Client;
import marmalade.client.Response;
import marmalade.client.handlers.ErrorHandler;
import marmalade.client.interceptors.PreemptiveAuthorizer;
import marmalade.spi.Registry;
import marmalade.util.RequestUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.conn.scheme.AsyncScheme;
import org.apache.http.nio.conn.ssl.SSLLayeringStrategy;
import org.apache.http.nio.entity.EntityAsyncContentProducer;
import org.apache.http.nio.entity.HttpAsyncContentProducer;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.IOReactorStatus;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParamBean;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.VersionInfo;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link AsyncClient}.
 * 
 */
public class DefaultAsyncClient extends BaseClient implements AsyncClient
{
   static class RequestProducerImpl extends BasicAsyncRequestProducer
   {
      public RequestProducerImpl(HttpHost target, HttpRequest request)
      {
         super(target, request);
      }

      protected RequestProducerImpl(HttpHost target, HttpEntityEnclosingRequest request,
            HttpAsyncContentProducer producer)
      {
         super(target, request, producer);
      }
   }

   private static HttpAsyncClient createDefaultHttpClient()
   {
      HttpAsyncClient httpClient;

      if (Registry.isRegitered(HttpAsyncClient.class)) {

         httpClient = Registry.lookup(HttpAsyncClient.class);

      } else {
         try {
            // Defaults to PoolingClientAsyncConnectionManager
            DefaultHttpAsyncClient hc = new DefaultHttpAsyncClient();
            HttpParams params = hc.getParams();
            HttpProtocolParamBean protocolBean = new HttpProtocolParamBean(params);
            VersionInfo versionInfo = VersionInfo.loadVersionInfo("marmalade", DefaultAsyncClient.class.getClassLoader());
            protocolBean.setUserAgent(String.format("Marmalade-AsyncHttpClient/%s", versionInfo.getRelease()));

            httpClient = hc;

         } catch (IOReactorException e) {
            throw new MarmaladeException(e);
         }
      }

      return httpClient;
   }

   private HttpAsyncClient httpClient;

   public DefaultAsyncClient()
   {
      this(createDefaultHttpClient());
   }

   public DefaultAsyncClient(HttpAsyncClient httpClient)
   {
      super();
      this.httpClient = httpClient;
   }

   public Client authPreemptive(String schemeName)
   {
      DefaultHttpAsyncClient httpClient = (DefaultHttpAsyncClient) getHttpClient();
      httpClient.addRequestInterceptor(new PreemptiveAuthorizer(schemeName), 0);
      return this;
   }

   @Override
   public HttpAsyncRequestProducer createAsyncProducer(HttpUriRequest request)
   {
      HttpAsyncRequestProducer producer;
      HttpHost target = URIUtils.extractHost(request.getURI());

      if (RequestUtil.isEnclosingEntity(request)) {
         producer = createRequestProducer(request, target);
      } else {
         producer = new RequestProducerImpl(target, request);
      }

      return producer;
   }

   @Override
   public <T> Future<T> execute(HttpAsyncRequestProducer producer, HttpAsyncResponseConsumer<T> consumer)
   {
      return execute(producer, consumer, null);
   }

   @Override
   public <T> Future<T> execute(HttpAsyncRequestProducer producer, HttpAsyncResponseConsumer<T> consumer,
         FutureCallback<T> futureCallback)
   {
      return execute(producer, consumer, prepareLocalContext(), futureCallback);
   }

   @Override
   public <T> Future<T> execute(HttpAsyncRequestProducer producer, HttpAsyncResponseConsumer<T> consumer,
         HttpContext context, FutureCallback<T> futureCallback)
   {
      return activateIfNeeded().execute(producer, consumer, context, futureCallback);
   }

   @Override
   public Future<Response> execute(HttpUriRequest request)
   {
      return execute(request, null);
   }

   @Override
   public Future<Response> execute(HttpUriRequest request, FutureCallback<Response> futureCallback)
   {
      return execute(request, prepareLocalContext(), futureCallback);
   }

   @Override
   public Future<Response> execute(HttpUriRequest request, HttpContext context, FutureCallback<Response> futureCallback)
   {
      return activateIfNeeded().execute(createAsyncProducer(request), new ResponseConsumer(), context, futureCallback);
   }

   public HttpAsyncClient getHttpClient()
   {
      return this.httpClient;
   }

   @Override
   public <T> Future<T> map(HttpUriRequest request, Class<T> type)
   {
      return map(Registry.NS_DEFAULT, request, type);
   }

   @Override
   public <T> Future<T> map(HttpUriRequest request, Class<T> type, ErrorHandler errorHandler)
   {
      return map(Registry.NS_DEFAULT, request, type, errorHandler);
   }

   @Override
   public <T> Future<T> map(HttpUriRequest request, Class<T> type, FutureCallback<T> futureCallback,
         ErrorHandler errorHandler)
   {
      return map(Registry.NS_DEFAULT, request, type, futureCallback, errorHandler);
   }

   @Override
   public <T> Future<T> map(HttpUriRequest request, Class<T> type, HttpContext context, ErrorHandler errorHandler)
   {
      return map(Registry.NS_DEFAULT, request, type, context, errorHandler);
   }

   @Override
   public <T> Future<T> map(HttpUriRequest request, Class<T> type, HttpContext context,
         FutureCallback<T> futureCallback, ErrorHandler errorHandler)
   {
      return map(Registry.NS_DEFAULT, request, type, context, futureCallback, errorHandler);
   }

   @Override
   public <T> Future<T> map(String namespace, HttpUriRequest request, Class<T> type)
   {
      return map(namespace, request, type, null);
   }

   @Override
   public <T> Future<T> map(String namespace, HttpUriRequest request, Class<T> type, ErrorHandler errorHandler)
   {
      return map(namespace, request, type, prepareLocalContext(), null, errorHandler);
   }

   @Override
   public <T> Future<T> map(String namespace, HttpUriRequest request, Class<T> type, FutureCallback<T> futureCallback,
         ErrorHandler errorHandler)
   {
      return map(namespace, request, type, prepareLocalContext(), futureCallback, errorHandler);
   }

   @Override
   public <T> Future<T> map(String namespace, HttpUriRequest request, Class<T> type, HttpContext context,
         ErrorHandler errorHandler)
   {
      return map(namespace, request, type, context, null, errorHandler);
   }

   @Override
   public <T> Future<T> map(String namespace, HttpUriRequest request, Class<T> type, HttpContext context,
         FutureCallback<T> futureCallback, ErrorHandler errorHandler)
   {
      return activateIfNeeded().execute(createAsyncProducer(request), new MapperConsumer<T>(namespace, type, errorHandler), context, futureCallback);
   }

   public void proxyAuthPref(String... authpref)
   {
      Preconditions.checkNotNull(authpref, "Please, specify a valid auth policy chain.");
      httpClient.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, Arrays.asList(authpref));
   }

   public void registerScheme(final AsyncScheme scheme)
   {
      httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
   }

   public AsyncClient reset()
   {
      shutdown();
      httpClient = createDefaultHttpClient();
      return this;
   }

   public void shutdown()
   {
      try {
         httpClient.shutdown();
      } catch (InterruptedException e) {
         //
      }
   }

   public void trustSelfSignedCertificates()
   {
      try {
         SSLLayeringStrategy sslls = new SSLLayeringStrategy(new TrustSelfSignedStrategy(), SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
         registerScheme(new AsyncScheme("https", 443, sslls));
      } catch (Exception e) {
         throw new MarmaladeException(e);
      }
   }

   public void unregisterScheme(final String name)
   {
      httpClient.getConnectionManager().getSchemeRegistry().unregister(name);
   }

   protected BasicHttpContext prepareLocalContext()
   {
      BasicHttpContext ctx = super.prepareLocalContext();
      // XXX check this
      ctx.removeAttribute(ClientContext.AUTH_CACHE);
      return ctx;
   }

   private HttpAsyncClient activateIfNeeded()
   {
      IOReactorStatus status = httpClient.getStatus();
      if (IOReactorStatus.INACTIVE.equals(status)) {
         httpClient.start();
      }
      return httpClient;
   }

   private HttpAsyncRequestProducer createRequestProducer(HttpUriRequest request, HttpHost target)
   {
      HttpAsyncRequestProducer producer;
      HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) request;
      HttpEntity entity = entityRequest.getEntity();

      if (HttpAsyncContentProducer.class.isAssignableFrom(entity.getClass())) {
         producer = new RequestProducerImpl(target, entityRequest, (HttpAsyncContentProducer) entity);
      } else {
         producer = new RequestProducerImpl(target, entityRequest, new EntityAsyncContentProducer(entity));
      }

      return producer;
   }

}
