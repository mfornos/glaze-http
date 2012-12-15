package marmalade.test.http;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public abstract class BaseHttpTest
{
   protected static final int PORT = 51234;

   protected static final String baseUrl = "http://localhost:" + PORT;

   protected MockHttpServer server;

   @AfterClass
   public void after()
   {
      try {
         server.stop();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @AfterMethod
   public void afterMethod()
   {
      server.verify();
   }

   @BeforeClass
   public void before()
   {
      try {
         server = new MockHttpServer(PORT);
         server.start();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @BeforeMethod
   public void beforeMethod()
   {
      try {
         server.clearConditions();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
