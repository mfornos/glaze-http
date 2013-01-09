package glaze;

/**
 * Marmalade runtime exception.
 * 
 */
public class GlazeException extends RuntimeException
{
   private static final long serialVersionUID = -9218260844229848084L;

   public GlazeException()
   {
      super();
   }

   public GlazeException(String message)
   {
      super(message);
   }

   public GlazeException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public GlazeException(Throwable cause)
   {
      super(cause);
   }
}
