package marmalade.client.async;

import java.util.concurrent.Future;

import marmalade.client.Client;
import marmalade.client.Response;
import marmalade.client.handlers.ErrorHandler;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

public interface AsyncClient extends Client
{

   HttpAsyncRequestProducer createAsyncProducer(HttpUriRequest request);

   <T> Future<T> execute(HttpAsyncRequestProducer producer, HttpAsyncResponseConsumer<T> consumer);

   <T> Future<T> execute(HttpAsyncRequestProducer producer, HttpAsyncResponseConsumer<T> consumer,
         FutureCallback<T> futureCallback);

   <T> Future<T> execute(HttpAsyncRequestProducer producer, HttpAsyncResponseConsumer<T> consumer, HttpContext context,
         FutureCallback<T> futureCallback);

   Future<Response> execute(HttpUriRequest request);

   Future<Response> execute(HttpUriRequest request, FutureCallback<Response> futureCallback);

   Future<Response> execute(HttpUriRequest request, HttpContext context, FutureCallback<Response> futureCallback);

   <T> Future<T> map(HttpUriRequest request, Class<T> type);
   
   <T> Future<T> map(HttpUriRequest request, Class<T> type, ErrorHandler errorHandler);

   <T> Future<T> map(HttpUriRequest request, Class<T> type, FutureCallback<T> futureCallback, ErrorHandler errorHandler);

   <T> Future<T> map(HttpUriRequest request, Class<T> type, HttpContext context, ErrorHandler errorHandler);

   <T> Future<T> map(HttpUriRequest request, Class<T> type, HttpContext context, FutureCallback<T> futureCallback, ErrorHandler errorHandler);

   AsyncClient reset();

}
