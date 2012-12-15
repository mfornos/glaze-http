package marmalade.client.interceptors;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.mockito.Mockito;
import org.testng.annotations.Test;

public class TestDebugInterceptor
{
   @Test
   public void test() throws HttpException, IOException
   {
      HttpRequest request = Mockito.mock(HttpRequest.class);
      HttpResponse response = Mockito.mock(HttpResponse.class);

      DebugInterceptor dbg = new DebugInterceptor();
      dbg.process(request, null);
      dbg.process(response, null);

      Mockito.verify(request, Mockito.only()).getAllHeaders();
   }
}
