package glaze.test.http;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockHttpServer
{
   private class ExpectationHandler implements Container
   {
      private final List<Condition> conditions;

      public ExpectationHandler()
      {
         conditions = Collections.synchronizedList(new ArrayList<Condition>());
      }

      public void addCondition(Condition root)
      {
         this.conditions.add(root);
      }

      public void clear()
      {
         conditions.clear();
      }

      public void handle(Request req, Response response)
      {
         String data = null;
         try {
            if (req.getContentLength() > 0) {
               data = req.getContent();
            }
         } catch (IOException e) {
         }

         ResponseBuilder found = null;
         for (Condition cond : conditions) {
            found = cond.match(req);
            if (found != null) {
               found.wrap(response, req, data);
               return;
            }
         }

         notFoundResponse(req, response, data);

      }

      public void verify()
      {
         for (Condition cond : conditions) {
            cond.checkSatisfied();
         }
      }

      private void notFoundResponse(Request req, Response response, String data)
      {
         response.setCode(500);
         response.set(HttpHeaders.CONTENT_TYPE, ContentType.DEFAULT_TEXT.toString());
         PrintStream body;
         try {
            body = response.getPrintStream();
            body.print("Received unexpected request " + req.getMethod() + ":" + req.getTarget() + " with data: " + data);
            body.close();
         } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
         }
      }

   }

   private static final Logger LOGGER = LoggerFactory.getLogger(MockHttpServer.class);

   private ExpectationHandler handler;

   private int port;

   private Connection connection;

   public MockHttpServer(int port)
   {
      this.port = port;
   }

   public void clearConditions()
   {
      handler.clear();
   }

   public MockHttpServer expect(ResponseBuilder responseBuilder)
   {
      handler.addCondition(responseBuilder.getRoot());
      return this;
   }

   public void start() throws Exception
   {
      handler = new ExpectationHandler();
      connection = new SocketConnection(handler);
      SocketAddress address = new InetSocketAddress(port);
      connection.connect(address);
   }

   public void stop() throws Exception
   {
      connection.close();
   }

   public void verify()
   {
      handler.verify();
   }

}
