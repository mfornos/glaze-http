package glaze.client.wire.tasks;

import glaze.client.Response;
import glaze.client.sync.SyncClient;
import glaze.spi.Registry;

import java.io.Serializable;
import java.util.concurrent.Callable;


import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Convenience class for remote distributed request execution.
 * 
 * @param <T>
 *           response type
 */
public abstract class CallableRequest<T extends Serializable> implements Callable<T>, Serializable
{
   public interface SerializableResponseCallback<T extends Serializable> extends Serializable
   {
      T onException(Exception e);

      T onResponse(T response);
   }

   private static final long serialVersionUID = -7750147708791590508L;

   private final SerializableRequest request;
   private SerializableResponseCallback<T> callback;

   public CallableRequest(HttpUriRequest request, SerializableResponseCallback<T> callback)
   {
      this.request = SerializableRequest.from(request);
      this.callback = callback;
   }

   @Override
   public T call() throws Exception
   {
      try {
         HttpRequestBase matReq = request.materialize();
         SyncClient defaultClient = defaultClient();
         T result = execute(defaultClient, matReq);
         return callback.onResponse(result);
      } catch (Exception e) {
         return callback.onException(e);
      }
   }

   public void setCallback(SerializableResponseCallback<T> callback)
   {
      this.callback = callback;
   }

   protected SyncClient defaultClient()
   {
      return Registry.lookup(SyncClient.class);
   }

   @SuppressWarnings("unchecked")
   protected T execute(SyncClient client, HttpUriRequest request)
   {
      Response response = client.execute(request);
      return (T) new SerializableResponse(response);
   }

   protected SerializableResponseCallback<T> getCallback()
   {
      return callback;
   }

   protected SerializableRequest getHttpRequest()
   {
      return request;
   }

}
