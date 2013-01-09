package glaze.test.data;

import java.io.Serializable;

public class Member implements Serializable
{
   private static final long serialVersionUID = 1L;

   public Member()
   {

   }

   public Member(String id)
   {
      this.id = id;
   }

   public String id;
}
