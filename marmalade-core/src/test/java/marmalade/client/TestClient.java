package marmalade.client;

import marmalade.Marmalade;
import marmalade.client.sync.DefaultSyncClient;
import marmalade.client.sync.SyncClient;
import marmalade.test.data.Member;
import marmalade.test.http.BaseHttpTest;
import marmalade.test.http.Condition;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestClient extends BaseHttpTest
{

   private SyncClient client;

   @AfterClass
   public void end()
   {
      client.shutdown();
   }

   @Test(timeOut = 5000)
   public void execute()
   {
      server.expect(Condition.when("POST").respond("{\"id\":\"hi\"}", ContentType.APPLICATION_JSON));

      Member member = new Member("hi");
      Response response = client.execute(Marmalade.Post(baseUrl + "/").bean(member).as(ContentType.APPLICATION_JSON).build());
      Assert.assertTrue(response.isNotError());

      server.expect(Condition.when("PUT").respond("{\"id\":\"hi\"}", ContentType.APPLICATION_JSON));

      response = client.execute(Marmalade.Put(baseUrl + "/").bean(member).as(ContentType.APPLICATION_JSON).build());
      Assert.assertTrue(response.isNotError());
   }

   @Test(timeOut = 5000)
   public void map()
   {
      server.expect(Condition.when("GET").respond("{\"id\":\"abcd\"}", ContentType.APPLICATION_JSON));

      Member member = client.map(new HttpGet(baseUrl + "/"), Member.class);
      Assert.assertEquals(member.id, "abcd");
   }

   // TODO test configuration

   @BeforeClass
   public void start()
   {
      this.client = new DefaultSyncClient();
   }
}
