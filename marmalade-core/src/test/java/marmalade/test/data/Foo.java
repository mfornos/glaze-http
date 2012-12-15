package marmalade.test.data;

import java.io.File;

import marmalade.mime.BinaryMultipart;
import marmalade.mime.TextMultipart;

public class Foo
{

   public Foo()
   {

   }

   @SuppressWarnings("unused")
   @BinaryMultipart
   private File attachment;

   @SuppressWarnings("unused")
   @BinaryMultipart(fileName = "tangerine.jpg", mime = "image/jpeg", name = "photo")
   private File pht;

   @SuppressWarnings("unused")
   @TextMultipart
   private String hello = "world!";

   @SuppressWarnings("unused")
   @TextMultipart(name = "ho", mime = "application/json")
   private String hi = "{\"num\":1}";

   public void setAttachment(File attachment)
   {
      this.attachment = attachment;
      this.pht = attachment;
   }

}
