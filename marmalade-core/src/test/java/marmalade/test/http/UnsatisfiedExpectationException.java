package marmalade.test.http;

public class UnsatisfiedExpectationException extends RuntimeException
{
   private static final long serialVersionUID = -6003072239642243697L;

   public UnsatisfiedExpectationException(Condition cond)
   {
      super(cond.toString());
   }

}
