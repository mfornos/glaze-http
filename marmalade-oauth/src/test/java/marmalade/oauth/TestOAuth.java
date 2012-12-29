package marmalade.oauth;

import static marmalade.Marmalade.Get;
import static marmalade.test.http.Expressions.regex;
import marmalade.client.Response;
import marmalade.client.sync.DefaultSyncClient;
import marmalade.client.sync.SyncClient;
import marmalade.oauth.util.OAuthClientHelper;
import marmalade.test.http.BaseHttpTest;
import marmalade.test.http.Condition;

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
