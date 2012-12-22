package marmalade.client.handlers;

import java.io.IOException;

import marmalade.MarmaladeException;
import marmalade.test.data.Bar;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestMapperResponseHandler
{

   @Test
   public void handle() throws IOException
   {
      HttpResponse response = Mockito.mock(HttpResponse.class);
      Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
      Mockito.when(response.getEntity()).thenReturn(new StringEntity("{\"hi\":1}", ContentType.TEXT_PLAIN));
      Mockito.when(response.getFirstHeader(HttpHeaders.CONTENT_TYPE)).thenReturn(new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType()));
      MapperResponseHandler<Bar> mrh = new MapperResponseHandler<Bar>(Bar.class);
      Assert.assertFalse(mrh.isOverriden());
      Assert.assertEquals(mrh.handleResponse(response).hi, 1);
   }

   @Test(expectedExceptions = MarmaladeException.class, expectedExceptionsMessageRegExp = "Unable to resolve mapper for type 'text/plain' in namespace 'default'")
   public void handleDefaultMime() throws IOException
   {
      HttpResponse response = Mockito.mock(HttpResponse.class);
      Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
      Mockito.when(response.getEntity()).thenReturn(new StringEntity("{\"hi\":1}"));
      MapperResponseHandler<Bar> mrh = new MapperResponseHandler<Bar>(Bar.class);
      Assert.assertFalse(mrh.isOverriden());
      Assert.assertEquals(mrh.handleResponse(response).hi, 1);
   }

   @Test(expectedExceptions = MarmaladeException.class)
   public void handleError() throws IOException
   {
      HttpResponse response = Mockito.mock(HttpResponse.class);
      Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 401, "Unauth"));

      MapperResponseHandler<Bar> mrh = new MapperResponseHandler<Bar>(Bar.class);
      mrh.handleResponse(response);
   }

   @Test
   public void handleOverride() throws IOException
   {
      HttpResponse response = Mockito.mock(HttpResponse.class);
      Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
      Mockito.when(response.getEntity()).thenReturn(new StringEntity("{\"hi\":1}", ContentType.TEXT_PLAIN));
      Mockito.when(response.getFirstHeader(HttpHeaders.CONTENT_TYPE)).thenReturn(new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_XML.getMimeType()));
      MapperResponseHandler<Bar> mrh = new MapperResponseHandler<Bar>(Bar.class, ContentType.APPLICATION_JSON);
      Assert.assertTrue(mrh.isOverriden());
      Assert.assertEquals(mrh.handleResponse(response).hi, 1);
   }

}
