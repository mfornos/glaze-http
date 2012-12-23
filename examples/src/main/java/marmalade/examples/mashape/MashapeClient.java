package marmalade.examples.mashape;

import java.io.IOException;
import java.math.BigInteger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import marmalade.client.async.DefaultAsyncClient;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

public class MashapeClient extends DefaultAsyncClient
{
   private class MashapeAuth implements HttpRequestInterceptor
   {
      private final String publicKey = MashapeConfig.getPublicKey();
      private final String privateKey = MashapeConfig.getPrivateKey();

      @Override
      public void process(HttpRequest req, HttpContext ctx) throws HttpException, IOException
      {
         String hash = getHMAC_SHA1(publicKey, privateKey);
         String headerValue = publicKey + ":" + hash;
         req.addHeader("X-Mashape-Authorization", Base64.encode(new String(headerValue.getBytes())).replace("\r\n", ""));
      }

      private String getHMAC_SHA1(String value, String key)
      {
         if (value == null || key == null || value.trim() == "" || key.trim() == "") {
            throw new RuntimeException("Please enter your Mashape keys in the constructor.");
         }
         try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes());

            String hmac = "";
            BigInteger hash = new BigInteger(1, rawHmac);
            hmac = hash.toString(16);
            if (hmac.length() % 2 != 0) {
               hmac = "0" + hmac;
            }
            return hmac;
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

   }

   public MashapeClient()
   {
      super();
      interceptRequest(new MashapeAuth(), 0);
   }
}
