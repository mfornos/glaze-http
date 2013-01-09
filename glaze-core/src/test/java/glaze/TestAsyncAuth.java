package glaze;

import glaze.Glaze;
import glaze.client.Response;
import glaze.client.async.AsyncClient;
import glaze.client.async.DefaultAsyncClient;
import glaze.test.http.BaseHttpTest;
import glaze.test.http.Condition;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.entity.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestAsyncAuth extends BaseHttpTest
{

   @Test(timeOut = 5000)
   public void clientAuthBasic() throws InterruptedException, ExecutionException
   {
      challengeFlow("/auth/basic", "Basic realm=\"Test Realm\"", "Basic aGVsbG86d29ybGQ=");

      AsyncClient client = new DefaultAsyncClient();
      client.auth(new UsernamePasswordCredentials("hello", "world"));

      try {
         Future<Response> fr = Glaze.Get(baseUrl + "/auth/basic").sendAsync(client);

         Response response = fr.get();
         Assert.assertEquals(response.status(), 200);
         Assert.assertEquals(response.asString(), "yellow");
      } finally {
         client.shutdown();
      }
   }

   @Test(timeOut = 5000)
   public void clientAuthDigest() throws InterruptedException, ExecutionException
   {
      challengeFlow("/auth/digest", "Digest algorithm=MD5,realm=\"Test Realm\",nonce=\"cafe3333\"", "Digest username=\"hello\", realm=\"Test Realm\", nonce=\"cafe3333\", uri=\"/auth/digest\", response=\"29fc4c354e7d43317f2c977f15be3849\", algorithm=\"MD5\"");

      AsyncClient client = new DefaultAsyncClient();
      client.auth(new UsernamePasswordCredentials("hello", "world"));

      try {
         Future<Response> fr = Glaze.Get(baseUrl + "/auth/digest").sendAsync(client);

         Response response = fr.get();
         Assert.assertEquals(response.status(), 200);
         Assert.assertEquals(response.asString(), "yellow");
      } finally {
         client.shutdown();
      }
   }

   private void challengeFlow(String path, String challenge, String answer)
   {

      // Order of preference in match
      server.expect(Condition.when("GET").path(path).header(HttpHeaders.AUTHORIZATION, answer).respond("yellow", ContentType.DEFAULT_TEXT));
      server.expect(Condition.when("GET").path(path).respond("unauthorized!", ContentType.DEFAULT_TEXT).status(HttpStatus.SC_UNAUTHORIZED).and(AUTH.WWW_AUTH, challenge));

   }

}
