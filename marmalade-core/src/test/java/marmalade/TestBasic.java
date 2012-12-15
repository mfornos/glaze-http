package marmalade;

import static marmalade.Marmalade.Delete;
import static marmalade.Marmalade.Get;
import static marmalade.Marmalade.Head;
import static marmalade.Marmalade.Options;
import static marmalade.Marmalade.Patch;
import static marmalade.Marmalade.Post;
import static marmalade.Marmalade.Put;
import static marmalade.Marmalade.Trace;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.APPLICATION_XML;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import marmalade.client.UriBuilder;
import marmalade.func.Closures.Closure;
import marmalade.test.data.Card;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.Test;

public class TestBasic
{

   @Test
   public void methods() throws URISyntaxException
   {
      URI uri = new URI("http://localhost");
      assertNotNull(Trace("http://www.google.com").build());
      assertNotNull(Options("http://www.google.com").build());
      assertNotNull(Head("http://www.google.com").build());
      assertNotNull(Patch("http://www.google.com").build());
      assertNotNull(Delete("http://www.google.com").buildSendCall());
      assertNotNull(Patch("http://www.google.com").buildMapCall(String.class));
      assertNotNull(Trace(uri).build());
      assertNotNull(Options(uri).build());
      assertNotNull(Head(uri).build());
      assertNotNull(Patch(uri).build());
      assertNotNull(Delete(uri).buildSendCall());
      assertNotNull(Patch(uri).buildMapCall(String.class));
   }

   @Test
   public void otherEntityBuilds() throws UnsupportedEncodingException
   {
      assertNotNull(Get("http://localhost").entity(new StringEntity("blah")).build());
      assertNotNull(Get("http://localhost").bean("blah").build());
      assertNotNull(Post("http://localhost").bean("blah").as(null).build());
   }

   @Test
   public void postMappings() throws ParseException, IOException
   {
      URI uri = UriBuilder.uriBuilderFrom("https://api.google.com/card").build();
      Card card = new Card("Hello wiz", "xxx xxx", "4e77e2920441f7000045755f");

      HttpPost post;

      post = (HttpPost) Post(uri).bean(card).build();
      assertEquals(EntityUtils.toString(post.getEntity()), "name=Hello+wiz&desc=xxx+xxx&idList=4e77e2920441f7000045755f");

      post = (HttpPost) Post(uri, APPLICATION_JSON).bean(card).build();
      assertEquals(EntityUtils.toString(post.getEntity()), "{\"name\":\"Hello wiz\",\"desc\":\"xxx xxx\",\"idList\":\"4e77e2920441f7000045755f\"}");

      post = (HttpPost) Post(uri).bean(card).as(APPLICATION_XML).build();
      assertEquals(EntityUtils.toString(post.getEntity()), "<Card xmlns=\"\"><name>Hello wiz</name><desc>xxx xxx</desc><idList>4e77e2920441f7000045755f</idList></Card>");

      post = (HttpPost) Post(uri.toASCIIString()).bean(card).build();
      assertEquals(EntityUtils.toString(post.getEntity()), "name=Hello+wiz&desc=xxx+xxx&idList=4e77e2920441f7000045755f");
   }

   @Test
   public void putMappings() throws ParseException, IOException
   {
      URI uri = UriBuilder.uriBuilderFrom("https://api.google.com/card").build();
      Card card = new Card("Hello wiz", "xxx xxx", "4e77e2920441f7000045755f");

      HttpPut put;

      put = (HttpPut) Put(uri).bean(card).build();
      assertEquals(EntityUtils.toString(put.getEntity()), "name=Hello+wiz&desc=xxx+xxx&idList=4e77e2920441f7000045755f");

      put = (HttpPut) Put(uri, APPLICATION_JSON).bean(card).build();
      assertEquals(EntityUtils.toString(put.getEntity()), "{\"name\":\"Hello wiz\",\"desc\":\"xxx xxx\",\"idList\":\"4e77e2920441f7000045755f\"}");

      put = (HttpPut) Put(uri).bean(card).as(APPLICATION_XML).build();
      assertEquals(EntityUtils.toString(put.getEntity()), "<Card xmlns=\"\"><name>Hello wiz</name><desc>xxx xxx</desc><idList>4e77e2920441f7000045755f</idList></Card>");

      put = (HttpPut) Put(uri.toASCIIString()).bean(card).build();
      assertEquals(EntityUtils.toString(put.getEntity()), "name=Hello+wiz&desc=xxx+xxx&idList=4e77e2920441f7000045755f");
   }

   @Test
   public void requestParameters()
   {
      HttpHost proxy = new HttpHost("localhost");
      HttpUriRequest request = Get("http://www.google.com").userAgent("test").viaProxy(proxy).socketTimeout(10).addHeader(HttpHeaders.ACCEPT, "bla/bla").build();

      assertEquals(request.getFirstHeader(HttpHeaders.ACCEPT).getValue(), "bla/bla");
      assertEquals(request.getParams().getParameter(CoreProtocolPNames.USER_AGENT), "test");
      assertEquals(request.getParams().getParameter(CoreConnectionPNames.SO_TIMEOUT), 10);
      assertEquals(request.getParams().getParameter(ConnRoutePNames.DEFAULT_PROXY), proxy);

      request = Get(UriBuilder.uriBuilderFrom("https://api.google.com/card").build()).addHeader(HttpHeaders.ACCEPT, "bla/bla").setCacheControl("xxx").version(HttpVersion.HTTP_1_0).config(CoreProtocolPNames.STRICT_TRANSFER_ENCODING, false).useExpectContinue().build();

      assertEquals(request.getFirstHeader(HttpHeaders.ACCEPT).getValue(), "bla/bla");
      assertEquals(request.getFirstHeader(HttpHeaders.CACHE_CONTROL).getValue(), "xxx");
      assertNotEquals(request.getParams().getParameter(CoreProtocolPNames.USER_AGENT), "test");
      assertEquals(request.getParams().getParameter(CoreProtocolPNames.PROTOCOL_VERSION).toString(), "HTTP/1.0");
      assertTrue(request.getParams().getBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false));
      assertFalse((Boolean) request.getParams().getParameter(CoreProtocolPNames.STRICT_TRANSFER_ENCODING));

      assertTrue(Head("http://www.google.com").config("hello", true).build().getParams().getBooleanParameter("hello", false));
      assertTrue(Patch("http://www.google.com").staleConnectionCheck(true).build().getParams().getBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false));

      assertNull(Get("http://api.google.com").config("a", "abc").removeConfig("a").build().getParams().getParameter("a"));

      request = Get("http://www.google.com").removeHeaders(HttpHeaders.ACCEPT).removeHeader(new BasicHeader(HttpHeaders.CACHE_CONTROL, "aaa")).build();
      assertEquals(request.getHeaders(HttpHeaders.ACCEPT).length, 0);
      assertEquals(request.getHeaders(HttpHeaders.CACHE_CONTROL).length, 0);

      Date date = new Date(0);
      request = Get("http://www.google.com").connectTimeout(10).elementCharset("UTF-8").setIfModifiedSince(date).setIfUnmodifiedSince(date).setDate(date).build();
      assertEquals(request.getParams().getParameter(CoreConnectionPNames.CONNECTION_TIMEOUT), 10);
      assertEquals(request.getParams().getParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET), "UTF-8");
      assertEquals(request.getLastHeader(HttpHeaders.DATE).getValue(), "Thu, 01 Jan 1970 00:00:00 GMT");
      assertEquals(request.getLastHeader(HttpHeaders.IF_MODIFIED_SINCE).getValue(), "Thu, 01 Jan 1970 00:00:00 GMT");
      assertEquals(request.getLastHeader(HttpHeaders.IF_UNMODIFIED_SINCE).getValue(), "Thu, 01 Jan 1970 00:00:00 GMT");

      request = Get("http://www.google.com").setDateFormat(new SimpleDateFormat("yyyy")).setDate(date).build();
      assertEquals(request.getLastHeader(HttpHeaders.DATE).getValue(), "1970");

      request = Get("http://127.0.0.1").setHeaders(new Header[] { new BasicHeader("a", "b"), new BasicHeader("a", "c") }).build();
      assertEquals(request.getHeaders("a").length, 2);
   }

   @Test
   public void withClosure()
   {
      HttpUriRequest request = Get("http://127.0.0.1").decorate(new Closure<HttpRequestBase>()
      {
         @Override
         public void on(HttpRequestBase value)
         {
            value.addHeader("custom", "custom");
         }
      }).build();

      assertEquals(request.getLastHeader("custom").getValue(), "custom");
   }

}
