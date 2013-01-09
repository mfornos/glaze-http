package glaze.client;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

/**
 * Builder to create url-encoded form content.
 * 
 */
public class Form
{
   public static Form newForm()
   {
      return new Form(Consts.ISO_8859_1);
   }

   public static Form newForm(Charset charset)
   {
      return new Form(charset);
   }

   private final List<NameValuePair> nvps;

   private Charset charset;

   private Form(Charset charset)
   {
      this.nvps = new ArrayList<NameValuePair>();
      this.charset = charset;
   }

   public Form add(int order, String name, String value)
   {
      nvps.add(order, new BasicNameValuePair(name, value));
      return this;
   }

   public Form add(String name, String value)
   {
      nvps.add(new BasicNameValuePair(name, value));
      return this;
   }

   public HttpEntity build()
   {
      return new UrlEncodedFormEntity(nvps, charset);
   }

   public Form charset(Charset charset)
   {
      this.charset = charset;
      return this;
   }
}
