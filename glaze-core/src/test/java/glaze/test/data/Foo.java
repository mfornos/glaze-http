package glaze.test.data;

import glaze.mime.BinaryMultipart;
import glaze.mime.TextMultipart;

import java.io.File;


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
