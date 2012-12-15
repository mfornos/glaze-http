package marmalade.client.async;

import java.util.concurrent.Future;

import marmalade.client.Client;

import org.apache.http.HttpResponse;
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

   Future<HttpResponse> execute(HttpUriRequest request);

   Future<HttpResponse> execute(HttpUriRequest request, FutureCallback<HttpResponse> futureCallback);

   Future<HttpResponse> execute(HttpUriRequest request, HttpContext context, FutureCallback<HttpResponse> futureCallback);

   <T> Future<T> map(HttpUriRequest request, Class<T> type);

   <T> Future<T> map(HttpUriRequest request, Class<T> type, FutureCallback<T> futureCallback);

   <T> Future<T> map(HttpUriRequest request, Class<T> type, HttpContext context);

   <T> Future<T> map(HttpUriRequest request, Class<T> type, HttpContext context, FutureCallback<T> futureCallback);

   AsyncClient reset();

}
