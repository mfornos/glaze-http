package glaze.client.async;

import glaze.client.Response;
import glaze.client.async.AsyncClient;
import glaze.client.async.AsyncMap;
import glaze.client.async.DefaultAsyncClient;
import glaze.spi.Registry;
import glaze.test.data.Member;
import glaze.test.http.BaseHttpTest;
import glaze.test.http.Condition;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HttpContext;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestAsyncClient extends BaseHttpTest
{

   static class MyResponseConsumer extends AsyncCharConsumer<Boolean>
   {

      @Override
      protected Boolean buildResult(final HttpContext context)
      {
         return Boolean.TRUE;
      }

      @Override
      protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl) throws IOException
      {
         while (buf.hasRemaining()) {
            System.out.print(buf.get());
         }
      }

      @Override
      protected void onResponseReceived(final HttpResponse response)
      {
      }

      @Override
      protected void releaseResources()
      {
      }

   }

   @Test(timeOut = 5000)
   public void basic() throws InterruptedException, ExecutionException, ParseException, IOException
   {
      server.expect(Condition.when("GET").respond("{\"id\":\"streaming\"}", ContentType.APPLICATION_JSON));

      AsyncClient client = new DefaultAsyncClient();
      try {
         Future<Response> future1 = client.execute(new HttpGet(baseUrl + "/"), null);
         Assert.assertEquals(future1.get().status(), HttpStatus.SC_OK);
      } finally {
         client.shutdown();
      }
   }

   @Test(timeOut = 5000)
   public void basicLoop() throws InterruptedException, ExecutionException, ParseException, IOException
   {
      server.expect(Condition.when("GET").respond("{\"id\":\"streaming\"}", ContentType.APPLICATION_JSON));

      AsyncClient client = new DefaultAsyncClient();
      try {
         for (int i = 0; i < 10; i++) {
            Future<Response> future1 = client.execute(new HttpGet(baseUrl + "/"), null);
            Assert.assertEquals(future1.get().status(), HttpStatus.SC_OK);
         }
      } finally {
         client.shutdown();
      }
   }

   @Test(timeOut = 5000)
   public void map() throws InterruptedException, ExecutionException
   {
      server.expect(Condition.when("GET").respond("{\"id\":\"hello\"}", ContentType.APPLICATION_JSON));

      DefaultAsyncClient client = new DefaultAsyncClient();
      try {
         Assert.assertEquals(client.map(new AsyncMap<Member>(Registry.NS_DEFAULT, new HttpGet(baseUrl + "/"), Member.class, null)).get().id, "hello");
      } finally {
         client.shutdown();
      }
   }

   @Test(timeOut = 5000)
   public void resetTest() throws InterruptedException, ExecutionException, ParseException, IOException
   {
      server.expect(Condition.when("GET").respond("{\"id\":\"streaming\"}", ContentType.APPLICATION_JSON));

      AsyncClient client = new DefaultAsyncClient();
      try {
         for (int i = 0; i < 10; i++) {
            Future<Response> future1 = client.execute(new HttpGet(baseUrl + "/"), null);
            Assert.assertEquals(future1.get().status(), HttpStatus.SC_OK);
            client.reset();
         }
      } finally {
         client.shutdown();
      }
   }

   @Test(timeOut = 5000)
   public void streaming() throws InterruptedException, ExecutionException, IOReactorException
   {
      server.expect(Condition.when("POST").respond("{\"id\":\"hello\"}", ContentType.APPLICATION_JSON));

      AsyncClient client = new DefaultAsyncClient();
      try {
         Future<Boolean> response = client.execute(client.createAsyncProducer(new HttpGet(baseUrl + "/")), new MyResponseConsumer());
         Assert.assertTrue(response.get());

         HttpPost request = new HttpPost(baseUrl + "/");
         request.setEntity(new NStringEntity("hello", ContentType.TEXT_PLAIN));
         response = client.execute(client.createAsyncProducer(request), new MyResponseConsumer());
         Assert.assertTrue(response.get());
      } finally {
         client.shutdown();
      }
   }

}
