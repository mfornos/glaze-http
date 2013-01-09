package glaze.oauth;

import static glaze.Glaze.Get;
import static glaze.test.http.Expressions.regex;
import glaze.client.Response;
import glaze.client.sync.DefaultSyncClient;
import glaze.client.sync.SyncClient;
import glaze.oauth.util.OAuthClientHelper;
import glaze.test.http.BaseHttpTest;
import glaze.test.http.Condition;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestOAuth extends BaseHttpTest
{

   @Test
   public void preemptive()
   {
      SyncClient client = new DefaultSyncClient();
      OAuthClientHelper.enablePreemptiveAuth(client);

      String answer = ".*oauth_signature=\".*\".*oauth_signature_method=\"HMAC-SHA1\",.*";
      server.expect(Condition.when("GET").path("/").header(HttpHeaders.AUTHORIZATION, regex(answer)).respond("yellow", ContentType.DEFAULT_TEXT));

      Response response = Get(baseUrl + "/").send(client);
      Assert.assertEquals(response.status(), HttpStatus.SC_OK);
   }

}
