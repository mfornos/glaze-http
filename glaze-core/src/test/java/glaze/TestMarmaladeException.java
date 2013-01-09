package glaze;

import glaze.GlazeException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestMarmaladeException
{
   @Test(timeOut = 5000)
   public void test()
   {
      Throwable cause = new Throwable();
      Assert.assertNull(new GlazeException().getMessage());
      Assert.assertEquals(new GlazeException("hi").getMessage(), "hi");
      Assert.assertEquals(new GlazeException(cause).getCause(), cause);
      Assert.assertEquals(new GlazeException("hi", cause).getCause(), cause);
   }
}
