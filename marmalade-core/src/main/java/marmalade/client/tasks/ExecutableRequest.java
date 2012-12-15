package marmalade.client.tasks;

import java.util.concurrent.Callable;

import marmalade.client.sync.SyncClient;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * Convenience class for multi-threaded request execution.
 * 
 * @param <T>
 *           response type
 */
public abstract class ExecutableRequest<T> implements Runnable, Callable<T>
{
   public interface RunnableRequestCallback<T>
   {
      void onException(Exception e);

      void onResponse(T response);
   }

   private final SyncClient client;
   private final HttpUriRequest request;
   private final HttpContext context;
   private RunnableRequestCallback<T> callback;

   public ExecutableRequest(SyncClient client, HttpUriRequest request, RunnableRequestCallback<T> callback)
   {
      this.client = client;
      this.request = request;
      this.context = new BasicHttpContext();
      this.callback = callback;
   }

   @Override
   public T call() throws Exception
   {
      return execute(client, request, context);
   }

   @Override
   public void run()
   {
      try {
         callback.onResponse(execute(client, request, context));
      } catch (Exception e) {
         callback.onException(e);
      }
   }

   public void setCallback(RunnableRequestCallback<T> callback)
   {
      this.callback = callback;
   }

   @SuppressWarnings("unchecked")
   protected T execute(SyncClient client, HttpUriRequest request, HttpContext context)
   {
      return (T) client.execute(request, context);
   }

}
