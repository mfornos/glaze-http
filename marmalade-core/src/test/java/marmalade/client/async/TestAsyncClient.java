package marmalade.client.async;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import marmalade.test.data.Member;
import marmalade.test.http.BaseHttpTest;
import marmalade.test.http.Condition;

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

   @Test
   public void basic() throws InterruptedException, ExecutionException, ParseException, IOException
   {
      server.expect(Condition.when("GET").respond("{\"id\":\"streaming\"}", ContentType.APPLICATION_JSON));

      AsyncClient client = new DefaultAsyncClient();
      try {
         Future<HttpResponse> future1 = client.execute(new HttpGet(baseUrl + "/"));
         Assert.assertEquals(future1.get().getStatusLine().getStatusCode(), HttpStatus.SC_OK);
      } finally {
         client.shutdown();
      }
   }

   @Test
   public void basicLoop() throws InterruptedException, ExecutionException, ParseException, IOException
   {
      server.expect(Condition.when("GET").respond("{\"id\":\"streaming\"}", ContentType.APPLICATION_JSON));

      AsyncClient client = new DefaultAsyncClient();
      try {
         for (int i = 0; i < 10; i++) {
            Future<HttpResponse> future1 = client.execute(new HttpGet(baseUrl + "/"));
            Assert.assertEquals(future1.get().getStatusLine().getStatusCode(), HttpStatus.SC_OK);
         }
      } finally {
         client.shutdown();
      }
   }

   @Test
   public void map() throws InterruptedException, ExecutionException
   {

      server.expect(Condition.when("GET").respond("{\"id\":\"hello\"}", ContentType.APPLICATION_JSON));

      DefaultAsyncClient client = new DefaultAsyncClient();

      ArrayList<Future<Member>> futures = new ArrayList<Future<Member>>();
      //for (int i = 0; i < 3; i++) {
         futures.add(client.map(new HttpGet(baseUrl + "/"), Member.class));
      //}

      for (Future<Member> m : futures) {
         Assert.assertEquals(m.get().id, "hello");
      }
   }

   @Test
   public void resetTest() throws InterruptedException, ExecutionException, ParseException, IOException
   {

      server.expect(Condition.when("GET").respond("{\"id\":\"streaming\"}", ContentType.APPLICATION_JSON));

      AsyncClient client = new DefaultAsyncClient();
      for (int i = 0; i < 10; i++) {
         Future<HttpResponse> future1 = client.execute(new HttpGet(baseUrl + "/"));
         Assert.assertEquals(future1.get().getStatusLine().getStatusCode(), HttpStatus.SC_OK);
         client.reset();
      }
   }

   @Test
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
