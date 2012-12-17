package marmalade;

import static marmalade.Marmalade.EndAsync;
import static marmalade.Marmalade.Get;
import static marmalade.test.http.Condition.when;
import static marmalade.test.http.Expressions.ANY;
import static marmalade.test.http.Producers.headers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import marmalade.client.Response;
import marmalade.test.http.BaseHttpTest;

import org.apache.http.HttpResponse;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestContextIndependence extends BaseHttpTest
{
   @Test(timeOut = 5000)
   public void asyncCookiesIndependence() throws InterruptedException, ExecutionException
   {
      server.expect(when("GET").path(ANY).respond(200).and("Set-Cookie", headers("PART_NUMBER=RIDING_ROCKET_%s; path=/", "count")));

      final Map<Integer, HttpContext> contexts = new HashMap<Integer, HttpContext>();

      for (int i = 0; i < 10; i++) {
         BasicHttpContext ctx = new BasicHttpContext();
         ctx.setAttribute(ClientContext.COOKIE_STORE, new BasicCookieStore());
         ctx.setAttribute("custom", i);
         contexts.put(i, ctx);
      }

      List<Future<HttpResponse>> tasks = new ArrayList<Future<HttpResponse>>();

      try {
         for (int i = 0; i < 10; i++) {
            final int c = i;
            tasks.add(Get(baseUrl + "/" + c).addHeader("count", c).sendAsync(contexts.get(c)));
         }

         for (Future<HttpResponse> t : tasks) {
            Response response = new Response(t.get());
            Assert.assertEquals(response.status(), 200);
            response.discardContent();
         }

         for (int i = 0; i < 10; i++) {
            HttpContext httpContext = contexts.get(i);
            RequestWrapper requestWrapper = (RequestWrapper) httpContext.getAttribute("http.request");
            Assert.assertEquals(requestWrapper.getURI().toString(), "/" + i);
            Assert.assertEquals(httpContext.getAttribute("custom"), i);
            BasicCookieStore cookieStore = (BasicCookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);
            List<Cookie> cookies = cookieStore.getCookies();
            for (Cookie c : cookies) {
               Assert.assertEquals(c.getValue(), "RIDING_ROCKET_" + i);
            }
         }

      } finally {
         EndAsync();
      }
   }

   @Test(timeOut = 5000)
   public void syncCookiesIndependence() throws InterruptedException, ExecutionException
   {
      server.expect(when("GET").path(ANY).respond(200).and("Set-Cookie", headers("PART_NUMBER=RIDING_ROCKET_%s; path=/", "count")));

      ExecutorService executor = Executors.newCachedThreadPool();

      final Map<Integer, HttpContext> contexts = new HashMap<Integer, HttpContext>();

      for (int i = 0; i < 10; i++) {
         BasicHttpContext ctx = new BasicHttpContext();
         ctx.setAttribute(ClientContext.COOKIE_STORE, new BasicCookieStore());
         ctx.setAttribute("custom", i);
         contexts.put(i, ctx);
      }

      List<Future<Response>> tasks = new ArrayList<Future<Response>>();

      for (int i = 0; i < 10; i++) {
         final int c = i;
         tasks.add(executor.submit(new Callable<Response>()
         {
            public Response call() throws Exception
            {
               return Get(baseUrl + "/" + c).addHeader("count", c).send(contexts.get(c));
            }
         }));
      }

      executor.shutdown();

      for (Future<Response> t : tasks) {
         Response response = t.get();
         Assert.assertEquals(response.status(), 200);
         response.discardContent();
      }

      for (int i = 0; i < 10; i++) {
         HttpContext httpContext = contexts.get(i);
         RequestWrapper requestWrapper = (RequestWrapper) httpContext.getAttribute("http.request");
         Assert.assertEquals(requestWrapper.getURI().toString(), "/" + i);
         Assert.assertEquals(httpContext.getAttribute("custom"), i);
         BasicCookieStore cookieStore = (BasicCookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);
         List<Cookie> cookies = cookieStore.getCookies();
         for (Cookie c : cookies) {
            Assert.assertEquals(c.getValue(), "RIDING_ROCKET_" + i);
         }
      }
   }
}
