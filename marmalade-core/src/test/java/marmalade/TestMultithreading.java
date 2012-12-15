package marmalade;

import static marmalade.Marmalade.Get;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import marmalade.client.Response;
import marmalade.client.sync.SyncClient;
import marmalade.test.data.Member;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestMultithreading
{

   private SyncClient client;

   @SuppressWarnings("unchecked")
   @Test
   public void call() throws InterruptedException, ExecutionException, TimeoutException
   {
      String[] uris = new String[] { "http://www.google.com", "https://api.trello.com/1/members/xxx?key=xxx" };

      List<Future<Member>> futures = new ArrayList<Future<Member>>();
      ExecutorService executor = Executors.newCachedThreadPool();

      for (String uri : uris) {
         Callable<Member> call = Get(uri).buildMapRequest(client, Member.class);
         Future<Member> future = executor.submit(call);
         futures.add(future);
      }

      executor.shutdown();

      for (Future<Member> fm : futures) {
         Member member = fm.get(1, TimeUnit.MINUTES);
         assertEquals(member.id, "abcdefg");
      }

      verify(client, times(2)).map(any(HttpUriRequest.class), any(HttpContext.class), any(Class.class));
   }

   @Test
   public void execute() throws InterruptedException
   {
      String[] uris = new String[] { "http://www.google.com", "https://www.yahoo.com", "http://wikipedia.com" };

      ExecutorService executor = Executors.newCachedThreadPool();
      for (String uri : uris) {
         executor.execute(Get(uri).buildExecRequest(client));
      }

      executor.shutdown();
      executor.awaitTermination(1, TimeUnit.MINUTES);

      verify(client, times(3)).execute(any(HttpUriRequest.class), any(HttpContext.class));
   }

   @BeforeTest
   @SuppressWarnings("unchecked")
   public void setUp()
   {
      client = mock(SyncClient.class);
      HttpResponse response = mock(HttpResponse.class);
      when(response.getEntity()).thenReturn(new StringEntity("hello", ContentType.DEFAULT_TEXT));
      when(response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK"));
      when(client.map(any(HttpUriRequest.class), any(HttpContext.class), any(Class.class))).thenReturn(new Member("abcdefg"));
      when(client.execute(any(HttpUriRequest.class), any(HttpContext.class))).thenReturn(new Response(response));
   }
}
