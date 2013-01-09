package glaze.test.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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

   protected String readFile(String fileName)
   {
      BufferedReader br = null;
      try {
         br = new BufferedReader(new FileReader(fileName));
         StringBuilder sb = new StringBuilder();
         String line;
         while ((line = br.readLine()) != null) {
            sb.append(line);
         }
         return sb.toString();
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      } finally {
         try {
            if (br != null)
               br.close();
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }
   }

   protected void writeFile(String fileName, String content)
   {
      BufferedWriter bw = null;
      try {
         bw = new BufferedWriter(new FileWriter(fileName));
         bw.write(content);
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         try {
            if (bw != null)
               bw.close();
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }
   }
}
