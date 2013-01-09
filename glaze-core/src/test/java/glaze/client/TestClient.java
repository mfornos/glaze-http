package glaze.client;

import glaze.Glaze;
import glaze.client.Response;
import glaze.client.sync.DefaultSyncClient;
import glaze.client.sync.SyncClient;
import glaze.client.sync.SyncMap;
import glaze.test.data.Member;
import glaze.test.http.BaseHttpTest;
import glaze.test.http.Condition;

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
      Response response = client.execute(Glaze.Post(baseUrl + "/").bean(member).as(ContentType.APPLICATION_JSON).build());
      Assert.assertTrue(response.isNotError());

      server.expect(Condition.when("PUT").respond("{\"id\":\"hi\"}", ContentType.APPLICATION_JSON));

      response = client.execute(Glaze.Put(baseUrl + "/").bean(member).as(ContentType.APPLICATION_JSON).build());
      Assert.assertTrue(response.isNotError());
   }

   @Test(timeOut = 5000)
   public void map()
   {
      server.expect(Condition.when("GET").respond("{\"id\":\"abcd\"}", ContentType.APPLICATION_JSON));

      Member member = client.map(new SyncMap<Member>(new HttpGet(baseUrl + "/"), Member.class));
      Assert.assertEquals(member.id, "abcd");
   }

   // TODO test configuration

   @BeforeClass
   public void start()
   {
      this.client = new DefaultSyncClient();
   }
}
