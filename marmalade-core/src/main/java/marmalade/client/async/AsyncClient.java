package marmalade.client.async;

import java.util.concurrent.Future;

import marmalade.client.Client;
import marmalade.client.Response;
import marmalade.client.handlers.ErrorHandler;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

/**
 * Interface for Marmalade asynchronous clients.
 * 
 */
public interface AsyncClient extends Client
{

   HttpAsyncRequestProducer createAsyncProducer(HttpUriRequest request);

   <T> Future<T> execute(HttpAsyncRequestProducer producer, HttpAsyncResponseConsumer<T> consumer);

   <T> Future<T> execute(HttpAsyncRequestProducer producer, HttpAsyncResponseConsumer<T> consumer,
         FutureCallback<T> futureCallback);

   <T> Future<T> execute(HttpAsyncRequestProducer producer, HttpAsyncResponseConsumer<T> consumer, HttpContext context,
         FutureCallback<T> futureCallback);

   Future<Response> execute(HttpUriRequest request, ErrorHandler errorHandler);

   Future<Response> execute(HttpUriRequest request, FutureCallback<Response> futureCallback, ErrorHandler errorHandler);

   Future<Response> execute(HttpUriRequest request, HttpContext context, FutureCallback<Response> futureCallback,
         ErrorHandler errorHandler);

   <T> Future<T> map(AsyncMap<T> mapRequest);

   AsyncClient reset();

   HttpAsyncClient getHttpClient();

}
