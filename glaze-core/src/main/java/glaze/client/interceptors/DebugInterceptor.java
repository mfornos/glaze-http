package glaze.client.interceptors;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugInterceptor implements HttpRequestInterceptor, HttpResponseInterceptor
{

   private static final Logger LOGGER = LoggerFactory.getLogger(DebugInterceptor.class);

   @Override
   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException
   {
      LOGGER.info("Request: {}\nContext: {} ", request.getAllHeaders(), context);
   }

   @Override
   public void process(HttpResponse response, HttpContext context) throws HttpException, IOException
   {
      LOGGER.info("Response: {}\nContext: {} ", response, context);
   }

}
