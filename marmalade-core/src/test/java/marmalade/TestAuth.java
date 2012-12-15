package marmalade;

import marmalade.client.Response;
import marmalade.client.sync.DefaultSyncClient;
import marmalade.client.sync.SyncClient;
import marmalade.test.http.BaseHttpTest;
import marmalade.test.http.Condition;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestAuth extends BaseHttpTest
{

   @Test
   public void clientAuthBasic()
   {
      challengeFlow("/auth/basic", "Basic realm=\"Test Realm\"", "Basic aGVsbG86d29ybGQ=");

      SyncClient client = new DefaultSyncClient();
      client.auth(new UsernamePasswordCredentials("hello", "world"));

      Response response = Marmalade.Get(baseUrl + "/auth/basic").send(client);

      Assert.assertEquals(response.status(), 200);
      Assert.assertEquals(response.asString(), "yellow");
   }

   @Test
   public void clientAuthDigest()
   {
      challengeFlow("/auth/digest", "Digest algorithm=MD5,realm=\"Test Realm\",nonce=\"cafe3333\"", "Digest username=\"hello\", realm=\"Test Realm\", nonce=\"cafe3333\", uri=\"/auth/digest\", response=\"29fc4c354e7d43317f2c977f15be3849\", algorithm=\"MD5\"");

      SyncClient client = new DefaultSyncClient();
      client.auth(new UsernamePasswordCredentials("hello", "world"));

      Response response = Marmalade.Get(baseUrl + "/auth/digest").send(client);

      Assert.assertEquals(response.status(), 200);
      Assert.assertEquals(response.asString(), "yellow");
   }

   @Test
   public void perRequestPreemptive()
   {
      HttpUriRequest request = Marmalade.Get("http://localhost").auth("hello", "world").build();
      Header auth = request.getFirstHeader(HttpHeaders.AUTHORIZATION);
      Assert.assertEquals(auth.getValue(), "Basic aGVsbG86d29ybGQ=");
   }

   private void challengeFlow(String path, String challenge, String answer)
   {

      // Order of preference in match
      server.expect(Condition.when("GET").path(path).header(HttpHeaders.AUTHORIZATION, answer).respond("yellow", ContentType.DEFAULT_TEXT));
      server.expect(Condition.when("GET").path(path).respond("unauthorized!", ContentType.DEFAULT_TEXT).status(HttpStatus.SC_UNAUTHORIZED).and(AUTH.WWW_AUTH, challenge));

   }

}
