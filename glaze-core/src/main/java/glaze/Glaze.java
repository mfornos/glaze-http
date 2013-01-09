/*
       ___  ___                           __          __   
      /   |/  /___ __________ ___  ____ _/ /___ _____/ /__ 
   __/  /|_/ / __ `/ ___/ __ `__ \/ __ `/ / __ `/ __  / _ \
  /    / /  / /_/ / /  / / / / / / /_/ / / /_/ / /_/ /  __/  HTTP delight
 /____/ /__/\__,_/_/  /_/ /_/ /_/\__,_/_/\__,_/\__,_/\___/   2012-2013 mfornos
 
 */

package glaze;

import glaze.client.Response;
import glaze.client.async.AsyncClient;
import glaze.client.async.AsyncMap;
import glaze.client.handlers.ErrorHandler;
import glaze.client.sync.SyncClient;
import glaze.client.sync.SyncMap;
import glaze.client.wire.tasks.MapCall;
import glaze.client.wire.tasks.SendCall;
import glaze.client.wire.tasks.SerializableResponse;
import glaze.client.wire.tasks.CallableRequest.SerializableResponseCallback;
import glaze.func.Closures.Closure;
import glaze.spi.Registry;
import glaze.util.EntityMapper;
import glaze.util.RequestUtil;
import glaze.util.TypeHelper;

import java.io.Serializable;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Future;


import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;

/**
 * <p>
 * Fluent API for comfortable HTTP interactions. Glaze offers a simple yet
 * complete toolkit to ease the building of powerful REST clients.
 * </p>
 * Provides:
 * <ul>
 * <li>Automatic serialization/deserialization by content type</li>
 * <li>Easy form handling, including multipart requests</li>
 * <li>Easy asynchronous HTTP interaction over NIO</li>
 * <li>Error handling</li>
 * <li>Convenient facilities for local multi-threaded and remote distributed
 * executors</li>
 * <li>Pluggable mappers and services</li>
 * </ul>
 * <h3>Examples</h3>
 * 
 * <strong>Mapping</strong>
 * 
 * <pre>
 * 
 * // Simple map
 * 
 * Map&lt;String, Object&gt; result = Get(uri).map();
 * 
 * // Map a bean
 * 
 * MyBean out = Get(uri).map(MyBean.class);
 * 
 * // Map with error handling
 * 
 * MyBean out = Get(uri).withErrorHandler(new ErrorHandler(){...}).map(MyBean.class);
 * 
 * // Post a bean as url-encoded content
 * 
 * Post(uri).bean(in).send();
 * 
 * // Post a bean as json
 * 
 * Post(uri).bean(in).as(APPLICATION_JSON).send();
 * 
 * // or
 * 
 * Post(uri, APPLICATION_JSON).bean(in).send();
 * 
 * // Post a bean as json and get the response mapped back to a bean according to
 * // the response content-type
 * 
 * MyBean out = Post(uri).bean(in).as(APPLICATION_JSON).map(MyBean.class);
 * 
 * // force to be mapped back from xml ignoring the response content-type. i.e.
 * // broken headers
 * 
 * Post(uri).bean(in).as(APPLICATION_JSON).map(MyBean.class, APPLICATION_XML);
 * 
 * </pre>
 * 
 * <strong>Asynchronous interaction</strong>
 * 
 * <pre>
 * 
 * // Basic send async
 * 
 * Future&lt;HttpResponse&gt; out = Get(uri).sendAsync();
 * out.get();
 * 
 * // Basic map async
 * 
 * Future&lt;MyBean&gt; out = Get(uri).mapAsync(MyBean.class);
 * out.get();
 * 
 * // With consumer
 * 
 * Future&lt;MyResult&gt; out = Get(uri).withConsumer(myAsyncConsumer).executeAsync();
 * out.get();
 * 
 * </pre>
 * 
 * <strong>Multipart</strong>
 * 
 * <pre>
 * 
 * // Post a file
 * 
 * Post(uri).bean(new File(&quot;myfile.png&quot;)).as(MULTIPART_FORM_DATA).send();
 * 
 * // or
 * 
 * Post(uri, MULTIPART_FORM_DATA).bean(new FileInputStream(file)).send();
 * 
 * // or bytes
 * 
 * byte[] bytes = new byte[] { 0x1, 0x1, 0x1, 0x0, 0xB, 0xA, 0xB, 0xB, 0xE };
 * 
 * Post(uri, MULTIPART_FORM_DATA).bean(bytes).send();
 * 
 * // Post a bean annotated with specific multipart annotations
 * 
 * class MultipartBean
 * {
 *    &#064;BinaryMultipart
 *    private File attachment;
 * 
 *    &#064;BinaryMultipart(fileName = &quot;tangerine.jpg&quot;, mime = &quot;image/jpeg&quot;, name = &quot;photo&quot;)
 *    private File pht;
 * 
 *    &#064;TextMultipart
 *    private String hello = &quot;world!&quot;;
 * 
 *    &#064;TextMultipart(name = &quot;ho&quot;, mime = &quot;application/json&quot;)
 *    private String hi = &quot;{\&quot;num\&quot;:1}&quot;;
 * }
 * 
 * Post(uri).bean(multipartBean).as(MULTIPART_FORM_DATA).send();
 * 
 * </pre>
 * 
 * @author mfornos
 * 
 */
public final class Glaze
{

   /** Default date format for HTTP headers. "EEE, dd MMM yyyy HH:mm:ss zzz" */
   public static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

   /** Default date locale. Locale.US */
   public static final Locale DATE_LOCALE = Locale.US;

   /** Default time zone. "GMT" */
   public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT");

   /**
    * Instantiates a builder for a DELETE request. See RFC 2616 section 9,
    * Method Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    */
   public static Glaze Delete(String uri)
   {
      return new Glaze(new HttpDelete(uri));
   }

   /**
    * Instantiates a builder for a DELETE request. See RFC 2616 section 9,
    * Method Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    */
   public static Glaze Delete(URI uri)
   {
      return new Glaze(new HttpDelete(uri));
   }

   /**
    * Instantiates a builder for a GET request. See RFC 2616 section 9, Method
    * Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    */
   public static Glaze Get(String uri)
   {
      return new Glaze(new HttpGet(uri));
   }

   /**
    * Instantiates a builder for a GET request. See RFC 2616 section 9, Method
    * Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    */
   public static Glaze Get(URI uri)
   {
      return new Glaze(new HttpGet(uri));
   }

   /**
    * Instantiates a builder for a HEAD request. See RFC 2616 section 9, Method
    * Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    */
   public static Glaze Head(String uri)
   {
      return new Glaze(new HttpHead(uri));
   }

   /**
    * Instantiates a builder for a HEAD request. See RFC 2616 section 9, Method
    * Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    */
   public static Glaze Head(URI uri)
   {
      return new Glaze(new HttpHead(uri));
   }

   /**
    * Instantiates a builder for a OPTIONS request. The OPTIONS method
    * represents a request for information about the communication options
    * available on the request/response chain identified by the Request-URI.
    * This method allows the client to determine the options and/or requirements
    * associated with a resource, or the capabilities of a server, without
    * implying a resource action or initiating a resource retrieval. See RFC
    * 2616 section 9, Method Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    */
   public static Glaze Options(String uri)
   {
      return new Glaze(new HttpOptions(uri));
   }

   /**
    * Instantiates a builder for a OPTIONS request. The OPTIONS method
    * represents a request for information about the communication options
    * available on the request/response chain identified by the Request-URI.
    * This method allows the client to determine the options and/or requirements
    * associated with a resource, or the capabilities of a server, without
    * implying a resource action or initiating a resource retrieval. See RFC
    * 2616 section 9, Method Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    */
   public static Glaze Options(URI uri)
   {
      return new Glaze(new HttpOptions(uri));
   }

   /**
    * Instantiates a builder for a PATCH request. The PATCH method is used to
    * apply partial modifications to a resource. See RFC 2616 section 9, Method
    * Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    */
   public static Glaze Patch(String uri)
   {
      return new Glaze(new HttpPatch(uri));
   }

   /**
    * Instantiates a builder for a PATCH request. The PATCH method is used to
    * apply partial modifications to a resource. See RFC 2616 section 9, Method
    * Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    */
   public static Glaze Patch(URI uri)
   {
      return new Glaze(new HttpPatch(uri));
   }

   /**
    * Instantiates a builder for a POST request. Defaults to
    * 'application-form-urlencoded' content-type. See RFC 2616 section 9, Method
    * Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    * @see #as(ContentType)
    */
   public static Glaze Post(String uri)
   {
      return Post(uri, ContentType.APPLICATION_FORM_URLENCODED);
   }

   /**
    * Instantiates a builder for a POST request with the given content-type. See
    * RFC 2616 section 9, Method Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @param contentType
    *           The content-type
    * @return builder instance
    * @see #as(ContentType)
    */
   public static Glaze Post(String uri, ContentType contentType)
   {
      return new Glaze(new HttpPost(uri)).as(contentType);
   }

   /**
    * Instantiates a builder for a POST request. Defaults to
    * 'application-form-urlencoded' content-type. See RFC 2616 section 9, Method
    * Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    * @see #as(ContentType)
    */
   public static Glaze Post(URI uri)
   {
      return Post(uri, ContentType.APPLICATION_FORM_URLENCODED);
   }

   /**
    * Instantiates a builder for a POST request with the given content-type. See
    * RFC 2616 section 9, Method Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @param contentType
    *           The content-type
    * @return builder instance
    * @see #as(ContentType)
    */
   public static Glaze Post(URI uri, ContentType contentType)
   {
      return new Glaze(new HttpPost(uri)).as(contentType);
   }

   /**
    * Instantiates a builder for a PUT request. Defaults to
    * 'application-form-urlencoded' content-type. See RFC 2616 section 9, Method
    * Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    * @see #as(ContentType)
    */
   public static Glaze Put(String uri)
   {
      return Put(uri, ContentType.APPLICATION_FORM_URLENCODED);
   }

   /**
    * Instantiates a builder for a PUT request with the given content-type. See
    * RFC 2616 section 9, Method Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @param contentType
    *           The content-type
    * @return builder instance
    * @see #as(ContentType)
    */
   public static Glaze Put(String uri, ContentType contentType)
   {
      return new Glaze(new HttpPut(uri)).as(contentType);
   }

   /**
    * Instantiates a builder for a PUT request. Defaults to
    * 'application-form-urlencoded' content-type. See RFC 2616 section 9, Method
    * Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    * @see #as(ContentType)
    */
   public static Glaze Put(URI uri)
   {
      return Put(uri, ContentType.APPLICATION_FORM_URLENCODED);
   }

   /**
    * Instantiates a builder for a POST request with the given content-type. See
    * RFC 2616 section 9, Method Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @param contentType
    *           The content-type
    * @return builder instance
    * @see #as(ContentType)
    */
   public static Glaze Put(URI uri, ContentType contentType)
   {
      return new Glaze(new HttpPut(uri)).as(contentType);
   }

   /**
    * Instantiates a builder for a TRACE request. The TRACE method is used to
    * invoke a remote, application-layer loop- back of the request message. See
    * RFC 2616 section 9, Method Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    */
   public static Glaze Trace(String uri)
   {
      return new Glaze(new HttpTrace(uri));
   }

   /**
    * Instantiates a builder for a TRACE request. The TRACE method is used to
    * invoke a remote, application-layer loop- back of the request message. See
    * RFC 2616 section 9, Method Definitions.
    * 
    * @param uri
    *           The URI of the request
    * @return builder instance
    */
   public static Glaze Trace(URI uri)
   {
      return new Glaze(new HttpTrace(uri));
   }

   private DateFormat dateFormatter;

   private final HttpRequestBase request;

   private final HttpParams localParams;

   private ContentType serializationType;

   private Object bean;

   private HttpEntity entity;

   private ErrorHandler errorHandler;

   private String namespace;

   private ResponseHandler<?> responseHandler;

   private Closure<HttpRequestBase> requestClosure;

   private HttpAsyncResponseConsumer<?> asyncConsumer;

   private boolean repeatable;

   private Glaze(HttpRequestBase request)
   {
      this.request = request;
      this.localParams = request.getParams();
      this.repeatable = false;
   }

   /**
    * Adds a HTTP header. The request-header fields allow the client to pass
    * additional information about the request, and about the client itself, to
    * the server.
    * 
    * @param header
    *           The HTTP header
    * @return builder instance
    * @see HttpHeaders
    */
   public Glaze addHeader(final Header header)
   {
      this.request.addHeader(header);
      return this;
   }

   /**
    * Adds a HTTP header. The request-header fields allow the client to pass
    * additional information about the request, and about the client itself, to
    * the server.
    * 
    * @param name
    *           The header name
    * @param value
    *           The header value
    * @return builder instance
    * @see HttpHeaders
    */
   public Glaze addHeader(final String name, final Object value)
   {
      return addHeader(name, value.toString());
   }

   /**
    * Adds a HTTP header. The request-header fields allow the client to pass
    * additional information about the request, and about the client itself, to
    * the server.
    * 
    * @param name
    *           The header name
    * @param value
    *           The header value
    * @return builder instance
    * @see HttpHeaders
    */
   public Glaze addHeader(final String name, final String value)
   {
      this.request.addHeader(name, value);
      return this;
   }

   /**
    * Specifies the type for content serialization and deserialization. Note
    * that the {@link ContentType} must match a mapper available in the
    * {@link Registry}.
    * 
    * @param contentType
    *           The content type
    * @return builder instance
    * @see ContentType
    */
   public Glaze as(ContentType contentType)
   {
      this.serializationType = contentType;
      return this;
   }

   /**
    * Authenticates the current request with basic-auth scheme. Per request
    * preemptive authentication. Generally, preemptive authentication can be
    * considered less secure than a response to an authentication challenge and
    * therefore discouraged.
    * 
    * @param username
    *           The user name
    * @param password
    *           The password
    * @return builder instance
    * @see SyncClient#auth(Credentials)
    */
   public Glaze auth(String username, String password)
   {
      UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
      return addHeader(BasicScheme.authenticate(creds, Consts.ASCII.name(), false));
   }

   /**
    * Specifies a bean to be sent as the content of the request, serialized
    * according to the given content type.
    * 
    * @param bean
    *           The bean to be serialized
    * @return builder instance
    * @see #as(ContentType)
    */
   public Glaze bean(Object bean)
   {
      this.bean = bean;
      return this;
   }

   /**
    * Builds the request.
    * 
    * @return the built HttpRequest
    */
   public HttpUriRequest build()
   {

      if (needsEntityMapping(request)) {
         entity = EntityMapper.map(namespace, bean, serializationType, repeatable);
      }

      // TODO repeatable
      if (entity != null && RequestUtil.isEnclosingEntity(request)) {
         RequestUtil.setEntity(request, entity);
      }

      if (requestClosure != null) {
         requestClosure.on(request);
      }

      return request;
   }

   /**
    * @param type
    *           The deserialization type
    * @return a request suitable for remote execution
    */
   public <T extends Serializable> MapCall<T> buildMapCall(Class<T> type)
   {
      return new MapCall<T>(build(), type);
   }

   /**
    * @param callback
    * @param type
    *           The deserialization type
    * @return a request suitable for remote execution
    */
   public <T extends Serializable> MapCall<T> buildMapCall(SerializableResponseCallback<T> callback, Class<T> type)
   {
      return new MapCall<T>(build(), callback, type);
   }

   /**
    * @return a request suitable for remote execution
    */
   public SendCall buildSendCall()
   {
      return new SendCall(build());
   }

   /**
    * @param callback
    * @return a request suitable for remote execution
    */
   public SendCall buildSendCall(SerializableResponseCallback<SerializableResponse> callback)
   {
      return new SendCall(build(), callback);
   }

   /**
    * Sets an arbitrary configuration parameter.
    * 
    * @param param
    *           The parameter name
    * @param object
    *           The parameter value
    * @return builder instance
    */
   public Glaze config(final String param, final Object object)
   {
      this.localParams.setParameter(param, object);
      return this;
   }

   /**
    * The timeout until a connection is established. A value of zero means the
    * timeout is not used.
    * 
    * @param timeout
    *           The timeout in milliseconds
    * @return builder instance
    */
   public Glaze connectTimeout(int timeout)
   {
      return config(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
   }

   /**
    * Decorates the request before send.
    * 
    * @param closure
    *           The closure
    * @return builder instance
    */
   public Glaze decorate(Closure<HttpRequestBase> closure)
   {
      this.requestClosure = closure;
      return this;
   }

   /**
    * The charset to be used for encoding/decoding HTTP protocol elements
    * (status line and headers).
    * 
    * @param charset
    *           The charset.
    * @return builder instance
    */
   public Glaze elementCharset(final String charset)
   {
      return config(CoreProtocolPNames.HTTP_ELEMENT_CHARSET, charset);
   }

   /**
    * Specifies the entity to be sent. It has preference over any bean already
    * set.
    * 
    * @param entity
    *           The HTTP entity
    * @return builder instance
    */
   public Glaze entity(HttpEntity entity)
   {
      this.entity = entity;
      return this;
   }

   /**
    * Executes the current request.
    * 
    * @return the response
    */
   public <T> T execute()
   {
      return execute(defaultSyncClient());
   }

   /**
    * Executes the current request with the given context.
    * 
    * @param context
    *           the HTTP context
    * @return the response
    */
   public <T> T execute(HttpContext context)
   {
      return execute(defaultSyncClient(), context);
   }

   /**
    * Executes the current request.
    * 
    * @param client
    *           The execution client.
    * @return the response
    */
   @SuppressWarnings("unchecked")
   public <T> T execute(SyncClient client)
   {
      return (T) (responseHandler == null ? client.execute(build(), errorHandler)
            : client.execute(request, responseHandler));
   }

   /**
    * Executes the current request.
    * 
    * @param client
    *           The execution client.
    * @param context
    *           The HTTP context.
    * @return the response
    */
   @SuppressWarnings("unchecked")
   public <T> T execute(SyncClient client, HttpContext context)
   {
      return (T) (responseHandler == null ? client.execute(build(), errorHandler, context)
            : client.execute(request, responseHandler, context));
   }

   public <T> Future<T> executeAsync()
   {
      return executeAsync(defaultAsyncClient());
   }

   @SuppressWarnings("unchecked")
   public <T> Future<T> executeAsync(AsyncClient client)
   {
      Preconditions.checkNotNull(asyncConsumer, "Please, specify an asynchronous consumer '.withConsumer(consumer)'.");

      HttpAsyncRequestProducer producer = client.createAsyncProducer(build());
      return (Future<T>) client.execute(producer, asyncConsumer);
   }

   /**
    * Maps the response to a {@link Map}.
    * 
    * @return a Map filled with values of the response
    */
   public Map<String, Object> map()
   {
      return map(TypeHelper.plainMap());
   }

   /**
    * Maps the response to a type instance.
    * 
    * @param type
    *           The class to be mapped.
    * @return an instance of the given type populated with response values.
    * @see #as(ContentType)
    */
   public <T> T map(Class<T> type)
   {
      return map(defaultSyncClient(), type);
   }

   /**
    * Maps the response to a type instance.
    * 
    * @param type
    *           The class to be mapped.
    * @param overrideType
    *           The type that overrides any ContentType already set.
    * @return an instance of the given type populated with response values.
    * @see #as(ContentType)
    */
   public <T> T map(Class<T> type, ContentType overrideType)
   {
      return map(defaultSyncClient(), type, overrideType);
   }

   /**
    * Maps the response to a type instance.
    * 
    * @param type
    *           The class to be mapped.
    * @param context
    *           The HttpContext of the request.
    * @return an instance of the given type populated with response values.
    */
   public <T> T map(Class<T> type, HttpContext context)
   {
      return map(defaultSyncClient(), type, context);
   }

   /**
    * Maps the response to a type instance.
    * 
    * @param type
    *           The class to be mapped.
    * @param context
    *           The HttpContext of the request.
    * @param overrideType
    *           The type that overrides any ContentType already set.
    * @return an instance of the given type populated with response values.
    */
   public <T> T map(Class<T> type, HttpContext context, ContentType overrideType)
   {
      return map(defaultSyncClient(), type, context, overrideType);
   }

   /**
    * Maps the response to a type instance.
    * 
    * @param client
    *           The execution client.
    * @param type
    *           The class to be mapped.
    * @return an instance of the given type populated with response values.
    */
   public <T> T map(SyncClient client, Class<T> type)
   {
      return client.map(new SyncMap<T>(namespace, build(), type, errorHandler));
   }

   /**
    * Maps the response to a type instance.
    * 
    * @param client
    *           The execution client.
    * @param type
    *           The class to be mapped.
    * @param overrideType
    *           The type that overrides any ContentType already set.
    * @return an instance of the given type populated with response values.
    */
   public <T> T map(SyncClient client, Class<T> type, ContentType overrideType)
   {
      return client.map(new SyncMap<T>(namespace, build(), type, errorHandler, overrideType));
   }

   /**
    * Maps the response to a type instance.
    * 
    * @param client
    *           The execution client.
    * @param type
    *           The class to be mapped.
    * @param context
    *           The HttpContext of the request.
    * @return an instance of the given type populated with response values.
    */
   public <T> T map(SyncClient client, Class<T> type, HttpContext context)
   {
      return client.map(new SyncMap<T>(namespace, build(), type, context, errorHandler));
   }

   /**
    * Maps the response to a type instance.
    * 
    * @param client
    *           The execution client.
    * @param type
    *           The class to be mapped.
    * @param context
    *           The HttpContext of the request.
    * @param overrideType
    *           The type that overrides any ContentType already set.
    * @return an instance of the given type populated with response values.
    */
   public <T> T map(SyncClient client, Class<T> type, HttpContext context, ContentType overrideType)
   {
      return client.map(new SyncMap<T>(namespace, build(), type, context, errorHandler, overrideType));
   }

   /**
    * Maps the response to a type instance.
    * 
    * @param client
    *           The execution client.
    * @param type
    *           The TypeReference to be mapped.
    * @return an instance of the given type populated with response values.
    * @see TypeReference
    */
   public <T> T map(SyncClient client, TypeReference<T> type)
   {
      return map(client, TypeHelper.resolveClass(type));
   }

   /**
    * Maps the response to a type instance.
    * 
    * @param type
    *           The TypeReference to be mapped.
    * @return an instance of the given type populated with response values.
    * @see TypeReference
    */
   public <T> T map(TypeReference<T> type)
   {
      return map(TypeHelper.resolveClass(type));
   }

   /**
    * Asynchronously maps the response to a type instance.
    * 
    * @param client
    *           The execution client.
    * @param type
    *           The class to be mapped.
    * @return an instance of the given type populated with response values.
    */
   public <T> Future<T> mapAsync(AsyncClient client, Class<T> type)
   {
      return client.map(new AsyncMap<T>(namespace, build(), type, errorHandler));
   }

   public <T> Future<T> mapAsync(AsyncClient client, Class<T> type, ContentType overrideType)
   {
      return client.map(new AsyncMap<T>(namespace, build(), type, errorHandler, overrideType));
   }

   /**
    * Asynchronously maps the response to a type instance.
    * 
    * @param client
    *           The execution client.
    * @param type
    *           The class to be mapped.
    * @param callback
    *           The FutureCallback.
    * @return an instance of the given type populated with response values.
    */
   public <T> Future<T> mapAsync(AsyncClient client, Class<T> type, FutureCallback<T> callback)
   {
      return client.map(new AsyncMap<T>(namespace, build(), type, callback, errorHandler));
   }

   /**
    * Asynchronously maps the response to a type instance.
    * 
    * @param client
    *           The execution client.
    * @param type
    *           The class to be mapped.
    * @param context
    *           The HttpContext of the request.
    * @return an instance of the given type populated with response values.
    */
   public <T> Future<T> mapAsync(AsyncClient client, Class<T> type, HttpContext context)
   {
      return client.map(new AsyncMap<T>(namespace, build(), type, context, errorHandler));
   }

   /**
    * Asynchronously maps the response to a type instance.
    * 
    * @param client
    *           The execution client.
    * @param type
    *           The class to be mapped.
    * @param context
    *           The HttpContext of the request.
    * @param callback
    *           The FutureCallback.
    * @return an instance of the given type populated with response values.
    */
   public <T> Future<T> mapAsync(AsyncClient client, Class<T> type, HttpContext context, FutureCallback<T> callback)
   {
      return client.map(new AsyncMap<T>(namespace, build(), type, context, callback, errorHandler));
   }

   public <T> Future<T> mapAsync(AsyncClient client, TypeReference<T> type)
   {
      return mapAsync(client, TypeHelper.resolveClass(type));
   }

   /**
    * Asynchronously maps the response to a type instance.
    * 
    * @param type
    *           The class to be mapped.
    * @return an instance of the given type populated with response values.
    */
   public <T> Future<T> mapAsync(Class<T> type)
   {
      return mapAsync(defaultAsyncClient(), type);
   }

   public <T> Future<T> mapAsync(Class<T> type, ContentType overrideType)
   {
      return mapAsync(defaultAsyncClient(), type, overrideType);
   }

   /**
    * Asynchronously maps the response to a type instance.
    * 
    * @param type
    *           The class to be mapped.
    * @param callback
    *           The FutureCallback.
    * @return an instance of the given type populated with response values.
    */
   public <T> Future<T> mapAsync(Class<T> type, FutureCallback<T> callback)
   {
      return mapAsync(defaultAsyncClient(), type, callback);
   }

   /**
    * Asynchronously maps the response to a type instance.
    * 
    * @param type
    *           The class to be mapped.
    * @param context
    *           The HttpContext of the request.
    * @return an instance of the given type populated with response values.
    */
   public <T> Future<T> mapAsync(Class<T> type, HttpContext context)
   {
      return mapAsync(defaultAsyncClient(), type, context);
   }

   /**
    * Asynchronously maps the response to a type instance.
    * 
    * @param type
    *           The class to be mapped.
    * @param context
    *           The HttpContext of the request.
    * @param callback
    *           The FutureCallback.
    * @return an instance of the given type populated with response values.
    */
   public <T> Future<T> mapAsync(Class<T> type, HttpContext context, FutureCallback<T> callback)
   {
      return mapAsync(defaultAsyncClient(), type, context, callback);
   }

   /**
    * @param namespace
    * @return builder instance
    */
   public Glaze ns(String namespace)
   {
      this.namespace = namespace;
      return this;
   }

   /**
    * Removes a request configuration parameter.
    * 
    * @param param
    *           The param name
    * @return builder instance
    */
   public Glaze removeConfig(final String param)
   {
      this.localParams.removeParameter(param);
      return this;
   }

   /**
    * Removes a HTTP header.
    * 
    * @see HttpHeaders
    * @param header
    *           The header
    * @return builder instance
    */
   public Glaze removeHeader(final Header header)
   {
      this.request.removeHeader(header);
      return this;
   }

   /**
    * Removes all HTTP headers with the given name.
    * 
    * @param name
    *           The header name
    * @return builder instance
    */
   public Glaze removeHeaders(final String name)
   {
      this.request.removeHeaders(name);
      return this;
   }

   public Glaze repeatable()
   {
      this.repeatable = true;
      return this;
   }

   /**
    * Executes the current request.
    * 
    * @return the response
    */
   public Response send()
   {
      return send(defaultSyncClient());
   }

   /**
    * Executes the current request with the given context.
    * 
    * @param context
    *           the HTTP context
    * @return the response
    */
   public Response send(HttpContext context)
   {
      return send(defaultSyncClient(), context);
   }

   /**
    * Executes the current request.
    * 
    * @param client
    *           The execution client.
    * @return the response
    */
   public Response send(SyncClient client)
   {
      Preconditions.checkArgument(responseHandler == null, "Response handler is not null, please use the 'execute' method instead of 'send'");

      return client.execute(build(), errorHandler);
   }

   /**
    * Executes the current request.
    * 
    * @param client
    *           The execution client.
    * @param context
    *           The HTTP context.
    * @return the response
    */
   public Response send(SyncClient client, HttpContext context)
   {
      Preconditions.checkArgument(responseHandler == null, "Response handler is not null, please use the 'execute' method instead of 'send'");

      return client.execute(build(), errorHandler, context);
   }

   /**
    * Executes the current request asynchronously.
    * 
    * @return the future response
    */
   public Future<Response> sendAsync()
   {
      return sendAsync(defaultAsyncClient());
   }

   /**
    * Executes the current request asynchronously.
    * 
    * @param client
    *           The execution client.
    * @return the future response
    */
   public Future<Response> sendAsync(AsyncClient client)
   {
      return client.execute(build(), errorHandler);
   }

   /**
    * Executes the current request asynchronously.
    * 
    * @param client
    *           The execution client.
    * @param callback
    *           The FutureCallback.
    * @return the future response
    */
   public Future<Response> sendAsync(AsyncClient client, FutureCallback<Response> callback)
   {
      return client.execute(build(), callback, errorHandler);
   }

   /**
    * Executes the current request asynchronously.
    * 
    * @param client
    *           The execution client.
    * @param context
    *           The HTTP context.
    * @return the future response
    */
   public Future<Response> sendAsync(AsyncClient client, HttpContext context)
   {
      return client.execute(build(), context, null, errorHandler);
   }

   /**
    * Executes the current request asynchronously.
    * 
    * @param client
    *           The execution client.
    * @param context
    *           The HTTP context.
    * @param callback
    *           The FutureCallback.
    * @return the future response
    */
   public Future<Response> sendAsync(AsyncClient client, HttpContext context, FutureCallback<Response> callback)
   {
      return client.execute(build(), context, callback, errorHandler);
   }

   /**
    * Executes the current request asynchronously.
    * 
    * @param callback
    *           The FutureCallback.
    * @return the future response
    */
   public Future<Response> sendAsync(FutureCallback<Response> callback)
   {
      return sendAsync(defaultAsyncClient(), callback);
   }

   /**
    * Executes the current request asynchronously with the given context.
    * 
    * @param context
    *           The HTTP context
    * @return the future response
    */
   public Future<Response> sendAsync(HttpContext context)
   {
      return sendAsync(defaultAsyncClient(), context);
   }

   /**
    * Executes the current request asynchronously.
    * 
    * @param context
    *           The HTTP context.
    * @param callback
    *           The FutureCallback.
    * @return the future response
    */
   public Future<Response> sendAsync(HttpContext context, FutureCallback<Response> callback)
   {
      return sendAsync(defaultAsyncClient(), context, callback);
   }

   /**
    * Specifies the accept HTTP header.
    * 
    * @param contentType
    *           The content type
    * @return builder instance
    */
   public Glaze setAccept(ContentType contentType)
   {
      return addHeader(HttpHeaders.ACCEPT, contentType.getMimeType());
   }

   /**
    * Specifies the cache-control HTTP header. Useful for proxied request. <br/>
    * HTTP 1.1. Allowed values = PUBLIC | PRIVATE | NO-CACHE | NO-STORE. <br/>
    * <ul>
    * <li>public - may be cached in public shared caches</li>
    * <li>private - may only be cached in private cache</li>
    * <li>no-cache - may not be cached</li>
    * <li>no-store - may be cached but not archived</li>
    * </ul>
    * 
    * @param cacheControl
    *           The cache-control value
    * @return builder instance
    */
   public Glaze setCacheControl(String cacheControl)
   {
      this.request.setHeader(HttpHeaders.CACHE_CONTROL, cacheControl);
      return this;
   }

   /**
    * Specifies the date HTTP header formatted by {@link #getDateFormat()}.
    * 
    * @param date
    *           The date
    * @return builder instance
    * @see #getDateFormat()
    */
   public Glaze setDate(final Date date)
   {
      this.request.setHeader(HttpHeaders.DATE, getDateFormat().format(date));
      return this;
   }

   /**
    * Sets the date format for HTTP headers.
    * 
    * @param format
    *           The date format
    * @return builder instance
    */
   public Glaze setDateFormat(DateFormat format)
   {
      this.dateFormatter = format;
      return this;
   }

   /**
    * Sets the HTTP headers.
    * 
    * @param headers
    *           Array of headers
    * @return builder instance
    */
   public Glaze setHeaders(final Header[] headers)
   {
      this.request.setHeaders(headers);
      return this;
   }

   /**
    * The If-Unmodified-Since request-header field is used with a method to make
    * it conditional. If the requested resource has not been modified since the
    * time specified in this field, the server SHOULD perform the requested
    * operation as if the If-Unmodified-Since header were not present. If the
    * requested variant has been modified since the specified time, the server
    * MUST NOT perform the requested operation, and MUST return a 412
    * (Precondition Failed).
    * 
    * @param date
    *           The date
    * @return builder instance
    */
   public Glaze setIfModifiedSince(final Date date)
   {
      this.request.setHeader(HttpHeaders.IF_MODIFIED_SINCE, getDateFormat().format(date));
      return this;
   }

   /**
    * The If-Modified-Since request-header field is used with a method to make
    * it conditional: if the requested variant has not been modified since the
    * time specified in this field, an entity will not be returned from the
    * server; instead, a 304 (not modified) response will be returned without
    * any message-body.
    * 
    * @param date
    *           The date
    * @return builder instance
    */
   public Glaze setIfUnmodifiedSince(final Date date)
   {
      this.request.setHeader(HttpHeaders.IF_UNMODIFIED_SINCE, getDateFormat().format(date));
      return this;
   }

   /**
    * Defines the socket timeout in milliseconds, which is the timeout for
    * waiting for data or, put differently, a maximum period inactivity between
    * two consecutive data packets. A timeout value of zero is interpreted as an
    * infinite timeout.
    * 
    * @param timeout
    *           The timeout in milliseconds.
    * @return builder instance
    */
   public Glaze socketTimeout(int timeout)
   {
      return config(CoreConnectionPNames.SO_TIMEOUT, timeout);
   }

   /**
    * Determines whether stale connection check is to be used. Disabling stale
    * connection check may result in a noticeable performance improvement (the
    * check can cause up to 30 millisecond overhead per request) at the risk of
    * getting an I/O error when executing a request over a connection that has
    * been closed at the server side. For performance critical operations the
    * check should be disabled.
    * 
    * @param b
    *           Boolean
    * @return builder instance
    */
   public Glaze staleConnectionCheck(boolean b)
   {
      return config(CoreConnectionPNames.STALE_CONNECTION_CHECK, b);
   }

   /**
    * Activates 'Expect: 100-Continue' handshake for the entity enclosing
    * methods. The 'Expect: 100-Continue' handshake allows a client that is
    * sending a request message with a request body to determine if the origin
    * server is willing to accept the request (based on the request headers)
    * before the client sends the request body. The use of the 'Expect:
    * 100-continue' handshake can result in noticeable performance improvement
    * for entity enclosing requests (such as POST and PUT) that require the
    * target server's authentication. 'Expect: 100-continue' handshake should be
    * used with caution, as it may cause problems with HTTP servers and proxies
    * that do not support HTTP/1.1 protocol.
    * 
    * @return builder instance
    */
   public Glaze useExpectContinue()
   {
      return config(CoreProtocolPNames.USE_EXPECT_CONTINUE, true);
   }

   /**
    * Configures the user agent string.
    * 
    * @param agent
    *           String identifying the user agent
    * @return builder instance
    */
   public Glaze userAgent(final String agent)
   {
      return config(CoreProtocolPNames.USER_AGENT, agent);
   }

   /**
    * Specifies the HTTP protocol version header.
    * 
    * @param version
    *           The protocol version
    * @return builder instance
    */
   public Glaze version(final HttpVersion version)
   {
      return config(CoreProtocolPNames.PROTOCOL_VERSION, version);
   }

   /**
    * Configures the default proxy host.
    * 
    * @param proxy
    *           The proxy host
    * @return builder instance
    */
   public Glaze viaProxy(final HttpHost proxy)
   {
      return config(ConnRoutePNames.DEFAULT_PROXY, proxy);
   }

   /**
    * @param consumer
    * @return builder instance
    */
   public <T> Glaze withConsumer(HttpAsyncResponseConsumer<T> consumer)
   {
      this.asyncConsumer = consumer;
      return this;
   }

   /**
    * Sets an error handler for the current request.
    * 
    * @param errorHandler
    *           The ErrorHandler.
    * @return builder instance
    * @see ErrorHandler
    */
   public Glaze withErrorHandler(ErrorHandler errorHandler)
   {
      this.errorHandler = errorHandler;
      return this;
   }

   /**
    * Sets a response handler for the current request.
    * 
    * @param responseHandler
    *           The ResponseHandler
    * @return builder instance
    * @see ResponseHandler
    */
   public Glaze withHandler(ResponseHandler<?> responseHandler)
   {
      this.responseHandler = responseHandler;
      return this;
   }

   private AsyncClient defaultAsyncClient()
   {
      return namespace == null ? Registry.lookup(AsyncClient.class) : Registry.lookup(namespace, AsyncClient.class);
   }

   private SyncClient defaultSyncClient()
   {
      return namespace == null ? Registry.lookup(SyncClient.class) : Registry.lookup(namespace, SyncClient.class);
   }

   private DateFormat getDateFormat()
   {
      if (this.dateFormatter == null) {
         this.dateFormatter = new SimpleDateFormat(DATE_FORMAT, DATE_LOCALE);
         this.dateFormatter.setTimeZone(TIME_ZONE);
      }
      return this.dateFormatter;
   }

   private boolean hasContentType()
   {
      return serializationType != null;
   }

   private boolean needsEntityMapping(HttpUriRequest request)
   {
      return entity == null && bean != null && RequestUtil.isEnclosingEntity(request) && hasContentType();
   }

}
