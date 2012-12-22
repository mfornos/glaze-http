package marmalade.client.wire.tasks;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import marmalade.util.RequestUtil;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpParamsNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Convenience class for HttpRequest serialization.
 * 
 */
public class SerializableRequest implements Serializable
{

   private static final Logger LOGGER = LoggerFactory.getLogger(SerializableRequest.class);

   private static final long serialVersionUID = 8168045456490905320L;

   public static SerializableRequest from(HttpRequestBase request)
   {
      return new SerializableRequest(request);
   }

   public static SerializableRequest from(HttpUriRequest req)
   {
      return from((HttpRequestBase) req);
   }

   private String method;

   private ListMultimap<String, String> headers;

   private Map<String, Object> params;

   private URI uri;

   private byte[] entityData;

   private String entityContentType;

   private String entityEncoding;

   public SerializableRequest(HttpRequestBase request)
   {
      this.uri = request.getURI();
      this.method = request.getRequestLine().getMethod();

      copyHeaders(request);
      copyParams(request);
      copyEntityData(request);
   }

   public boolean hasData()
   {
      return entityData != null;
   }

   public HttpRequestBase materialize()
   {
      HttpRequestBase req = extractRequest();
      req.setURI(uri);
      addHeaders(req);
      addParams(req);

      return req;
   }

   private void addHeaders(HttpRequestBase req)
   {
      for (Map.Entry<String, String> entry : headers.entries()) {
         req.addHeader(entry.getKey(), entry.getValue());
      }
   }

   private void addParams(HttpRequestBase req)
   {
      for (Map.Entry<String, Object> entry : params.entrySet()) {
         req.getParams().setParameter(entry.getKey(), entry.getValue());
      }
   }

   private void copyEntity(HttpEntity entity) throws IOException
   {
      this.entityData = EntityUtils.toByteArray(entity);
      Header contentType = entity.getContentType();
      if (contentType != null) {
         this.entityContentType = contentType.getValue();
      }
      Header contentEncoding = entity.getContentEncoding();
      if (contentEncoding != null) {
         this.entityEncoding = contentEncoding.getValue();
      }
   }

   private void copyEntityData(HttpRequestBase request)
   {
      // TODO handle multipart
      try {
         HttpEntity entity = RequestUtil.getEntity(request);

         if (entity != null) {
            copyEntity(entity);
         }
      } catch (IOException e) {
         LOGGER.error(e.getMessage(), e);
      }
   }

   private void copyHeaders(HttpRequestBase request)
   {
      this.headers = LinkedListMultimap.create();
      Header[] allHeaders = request.getAllHeaders();
      for (Header h : allHeaders) {
         this.headers.put(h.getName(), h.getValue());
      }
   }

   private void copyParams(HttpRequestBase request)
   {
      this.params = new HashMap<String, Object>();
      HttpParams rp = request.getParams();
      HttpParamsNames names = (HttpParamsNames) rp;
      for (String name : names.getNames()) {
         params.put(name, rp.getParameter(name));
      }
   }

   private HttpRequestBase extractEntityEnclosingRequest()
   {
      HttpRequestBase req;
      req = new HttpEntityEnclosingRequestBase()
      {
         @Override
         public String getMethod()
         {
            return method;
         }
      };
      ByteArrayEntity entity = new ByteArrayEntity(entityData);
      if (entityEncoding != null) {
         entity.setContentEncoding(entityEncoding);
      }
      if (entityContentType != null) {
         entity.setContentType(entityContentType);
      }
      ((HttpEntityEnclosingRequest) req).setEntity(entity);
      return req;
   }

   private HttpRequestBase extractRequest()
   {
      HttpRequestBase req;
      if (hasData()) {
         req = extractEntityEnclosingRequest();
      } else {
         req = new HttpRequestBase()
         {
            @Override
            public String getMethod()
            {
               return method;
            }
         };
      }
      return req;
   }
}
