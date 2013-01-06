package marmalade.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import marmalade.Marmalade;
import marmalade.MarmaladeException;
import marmalade.client.handlers.CroakErrorHandler;
import marmalade.client.sync.DefaultSyncClient;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestRetry
{
   private static class TestRetryHandler implements HttpRequestRetryHandler
   {

      public int retries;

      public boolean retryRequest(IOException exception, int executionCount, HttpContext context)
      {
         this.retries = executionCount;

         if (executionCount >= 2) {
            // Do not retry if over max retry count
            return false;
         }
         if (exception instanceof InterruptedIOException) {
            // Timeout
            return false;
         }
         if (exception instanceof UnknownHostException) {
            // Unknown host
            return false;
         }
         if (exception instanceof ConnectException) {
            // Connection refused
            return true;
         }
         if (exception instanceof SSLException) {
            // SSL handshake exception
            return false;
         }
         HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
         boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
         if (idempotent) {
            // Retry if the request is considered idempotent
            return true;
         }
         return false;
      }

   }

   @Test
   public void test() throws InterruptedException
   {
      DefaultSyncClient client = new DefaultSyncClient();
      TestRetryHandler testRetryHandler = new TestRetryHandler();
      client.retryHandler(testRetryHandler);

      try {
         client.execute(Marmalade.Get("http://127.0.0.1:12345/").build());
      } catch (MarmaladeException e) {
         //
      } finally {
         client.shutdown();
      }

      Assert.assertEquals(testRetryHandler.retries, 2);
   }

   @Test
   public void testErrorHandler() throws InterruptedException
   {
      DefaultSyncClient client = new DefaultSyncClient();
      TestRetryHandler testRetryHandler = new TestRetryHandler();
      client.retryHandler(testRetryHandler);

      try {
         client.execute(Marmalade.Get("http://127.0.0.1:12345/").withErrorHandler(new CroakErrorHandler()).build());
      } catch (MarmaladeException e) {
         //
      } finally {
         client.shutdown();
      }

      Assert.assertEquals(testRetryHandler.retries, 2);
   }
}
