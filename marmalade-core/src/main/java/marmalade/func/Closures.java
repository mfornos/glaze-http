package marmalade.func;

/**
 * Parameterized closures.
 * 
 */
public interface Closures
{
   /**
    * Closure without return value.
    * 
    * @param <T>
    *           argument type
    */
   public static interface Closure<T>
   {
      void on(T value);
   }

   /**
    * Closure with return value.
    * 
    * @param <R>
    *           return type
    * @param <T>
    *           argument type
    */
   public static interface ResponseClosure<R, T>
   {
      R on(T value);
   }
}
