package marmalade;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestMarmaladeException
{
   @Test
   public void test()
   {
      Throwable cause = new Throwable();
      Assert.assertNull(new MarmaladeException().getMessage());
      Assert.assertEquals(new MarmaladeException("hi").getMessage(), "hi");
      Assert.assertEquals(new MarmaladeException(cause).getCause(), cause);
      Assert.assertEquals(new MarmaladeException("hi", cause).getCause(), cause);
   }
}
