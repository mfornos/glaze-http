package marmalade;

/**
 * Marmalade runtime exception.
 * 
 */
public class MarmaladeException extends RuntimeException
{
   private static final long serialVersionUID = -9218260844229848084L;

   public MarmaladeException()
   {
      super();
   }

   public MarmaladeException(String message)
   {
      super(message);
   }

   public MarmaladeException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public MarmaladeException(Throwable cause)
   {
      super(cause);
   }
}
