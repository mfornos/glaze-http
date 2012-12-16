package marmalade.client.sync;

import marmalade.client.Client;
import marmalade.client.Response;
import marmalade.client.handlers.ErrorHandler;
import marmalade.func.Closures.ResponseClosure;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HttpContext;

/**
 * Contract for execution and mapping of requests.
 * 
 */
public interface SyncClient extends Client
{

   /**
    * Executes the request. Please, note that response content must be processed
    * or discarded using {@link Response#discardContent()}, otherwise the
    * connection used for the request might not be released to the pool.
    * 
    * @see Response#asString()
    * @see Response#asInputStream()
    * @see Response#with(ResponseClosure)
    * @see Response#discardContent()
    */
   Response execute(HttpUriRequest request);

   Response execute(HttpUriRequest build, ErrorHandler errorHandler);

   Response execute(HttpUriRequest build, ErrorHandler errorHandler, HttpContext context);

   /**
    * Executes the request. Please, note that response content must be processed
    * or discarded using {@link Response#discardContent()}, otherwise the
    * connection used for the request might not be released to the pool.
    * 
    * @see Response#asString()
    * @see Response#asInputStream()
    * @see Response#with(ResponseClosure)
    * @see Response#discardContent()
    */
   Response execute(HttpUriRequest request, HttpContext context);

   /**
    * @param request
    * @param handler
    * @return
    */
   <T> T execute(HttpUriRequest request, ResponseHandler<T> handler);

   /**
    * @param request
    * @param handler
    * @param context
    * @return
    */
   <T> T execute(HttpUriRequest request, ResponseHandler<T> handler, HttpContext context);

   /**
    * @return underlying HttpClient
    */
   HttpClient getHttpClient();

   /**
    * @param request
    * @param type
    * @return
    */
   <T> T map(HttpUriRequest request, Class<T> type);

   /**
    * @param request
    * @param type
    * @param forceType
    * @return
    */
   <T> T map(HttpUriRequest request, Class<T> type, ContentType forceType);

   <T> T map(HttpUriRequest request, Class<T> type, ErrorHandler errorHandler);

   /**
    * @param request
    * @param context
    * @param type
    * @return
    */
   <T> T map(HttpUriRequest request, HttpContext context, Class<T> type);

   /**
    * @param request
    * @param context
    * @param type
    * @param forceType
    * @return
    */
   <T> T map(HttpUriRequest request, HttpContext context, Class<T> type, ContentType forceType);

   /**
    * Specifies the authorization chain.
    * 
    * @param authpref
    *           An ordered list of authorization schemes
    */
   void proxyAuthPref(String... authpref);

   /**
    * Registers a scheme on the underlying connection manager.
    * 
    * @param scheme
    *           The scheme name
    */
   void registerScheme(final Scheme scheme);

   /**
    * 
    */
   void trustSelfSignedCertificates();

   /**
    * Unregisters a scheme on the underlying connection manager.
    * 
    * @param name
    *           The scheme name
    */
   void unregisterScheme(final String name);

}
