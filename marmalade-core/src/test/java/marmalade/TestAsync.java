package marmalade;

import static marmalade.Marmalade.*;
import static marmalade.test.http.Condition.when;
import static marmalade.test.http.Expressions.any;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import marmalade.client.async.AsyncClient;
import marmalade.client.async.DefaultAsyncClient;
import marmalade.test.data.Card;
import marmalade.test.data.Member;
import marmalade.test.http.BaseHttpTest;
import marmalade.test.http.Condition;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncByteConsumer;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestAsync extends BaseHttpTest
{

   private class CounterCallback<T> implements FutureCallback<T>
   {
      final AtomicInteger counter;

      public CounterCallback()
      {
         counter = new AtomicInteger();
      }

      @Override
      public void cancelled()
      {
         //
      }

      @Override
      public void completed(T response)
      {
         counter.incrementAndGet();
      }

      @Override
      public void failed(Exception arg0)
      {
         //
      }
   }

   @Test
   public void postJson() throws InterruptedException, ExecutionException
   {
      server.expect(Condition.when("POST").path("/scott").respond("{\"id\":\"scott\"}", ContentType.APPLICATION_JSON));
      server.expect(Condition.when("POST").path("/tiger").respond("{\"id\":\"tiger\"}", ContentType.APPLICATION_JSON));

      Card in = new Card("Hello", "world", "0989080");

      Future<Member> scott = Post(baseUrl + "/scott").bean(in).as(APPLICATION_JSON).mapAsync(Member.class);
      Future<Member> tiger = Post(baseUrl + "/tiger", APPLICATION_JSON).bean(in).mapAsync(Member.class, new BasicHttpContext());

      Assert.assertEquals(scott.get().id, "scott");
      Assert.assertEquals(tiger.get().id, "tiger");
   }

   @Test
   public void postJsonCallback() throws InterruptedException, ExecutionException
   {
      server.expect(Condition.when("POST").path("/scott").respond("{\"id\":\"scott\"}", ContentType.APPLICATION_JSON));
      server.expect(Condition.when("POST").path("/tiger").respond("{\"id\":\"tiger\"}", ContentType.APPLICATION_JSON));

      CounterCallback<Member> callback = new CounterCallback<Member>();

      Card in = new Card("Hello", "world", "0989080");

      Future<Member> scott = Post(baseUrl + "/scott").bean(in).as(APPLICATION_JSON).mapAsync(Member.class, callback);
      Future<Member> tiger = Post(baseUrl + "/tiger", APPLICATION_JSON).bean(in).mapAsync(Member.class, new BasicHttpContext(), callback);

      Assert.assertEquals(scott.get().id, "scott");
      Assert.assertEquals(tiger.get().id, "tiger");

      Assert.assertEquals(callback.counter.get(), 2);
   }

   @Test
   public void send() throws InterruptedException, ExecutionException
   {
      server.expect(when(any()).path("/areq").respond("OK"));

      AsyncClient client = new DefaultAsyncClient();

      List<Future<HttpResponse>> futures = new ArrayList<Future<HttpResponse>>();

      futures.add(Marmalade.Post(baseUrl + "/areq").bean("hello").as(ContentType.DEFAULT_TEXT).sendAsync());
      futures.add(Marmalade.Get(baseUrl + "/areq").sendAsync(new BasicHttpContext()));
      futures.add(Marmalade.Put(baseUrl + "/areq").bean("hello").as(ContentType.DEFAULT_TEXT).sendAsync(client, new BasicHttpContext()));
      futures.add(Marmalade.Head(baseUrl + "/areq").sendAsync(client));
      futures.add(Marmalade.Delete(baseUrl + "/areq").sendAsync());

      for (Future<HttpResponse> resp : futures) {
         Assert.assertEquals(resp.get().getStatusLine().getStatusCode(), HttpStatus.SC_OK);
      }

   }

   @Test
   public void sendCallback() throws InterruptedException, ExecutionException
   {
      server.expect(when(any()).path("/areq").respond("OK"));

      AsyncClient client = new DefaultAsyncClient();

      List<Future<HttpResponse>> futures = new ArrayList<Future<HttpResponse>>();

      CounterCallback<HttpResponse> callback = new CounterCallback<HttpResponse>();

      futures.add(Marmalade.Post(baseUrl + "/areq").bean("hello").as(ContentType.DEFAULT_TEXT).sendAsync(callback));
      futures.add(Marmalade.Get(baseUrl + "/areq").sendAsync(client, callback));
      futures.add(Marmalade.Put(baseUrl + "/areq").bean("hello").as(ContentType.DEFAULT_TEXT).sendAsync(new BasicHttpContext(), callback));
      futures.add(Marmalade.Head(baseUrl + "/areq").sendAsync(client, new BasicHttpContext(), callback));
      futures.add(Marmalade.Delete(baseUrl + "/areq").sendAsync(callback));

      for (Future<HttpResponse> resp : futures) {
         Assert.assertEquals(resp.get().getStatusLine().getStatusCode(), HttpStatus.SC_OK);
      }

      Assert.assertEquals(callback.counter.get(), 5);

   }

   @Test
   public void stream() throws InterruptedException, ExecutionException
   {
      server.expect(Condition.when("GET").path("/stream").respond("Michael bytes", ContentType.APPLICATION_JSON));

      Future<String> ok = Get(baseUrl + "/stream").stream(new AsyncByteConsumer<String>()
      {
         private String ok;

         @Override
         protected String buildResult(HttpContext ctx) throws Exception
         {
            return ok;
         }

         @Override
         protected void onByteReceived(ByteBuffer bytes, IOControl control) throws IOException
         {
            ok = "";
            byte[] bb = bytes.array();
            for (byte b : bb) {
               if (b > 0)
                  ok += (char) b;
            }
         }

         @Override
         protected void onResponseReceived(HttpResponse response) throws HttpException, IOException
         {
            //
         }
      });

      Assert.assertEquals(ok.get(), "Michael bytes");

   }

}
