package marmalade.client.handlers;

import java.io.IOException;

import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

public class AdaptiveRetryHandler extends StandardHttpRequestRetryHandler
{
   private final long waitTime;

   public AdaptiveRetryHandler(int retryCount, long waitTime, boolean requestSentRetryEnabled)
   {
      super(retryCount, requestSentRetryEnabled);
      this.waitTime = waitTime;
   }

   @Override
   public boolean retryRequest(IOException exception, int executionCount, HttpContext context)
   {
      boolean retry = super.retryRequest(exception, executionCount, context);

      if (retry) {
         try {
            Thread.sleep(waitTime * executionCount);
         } catch (InterruptedException e) {
            //
         }
      }

      return retry;
   }

}
