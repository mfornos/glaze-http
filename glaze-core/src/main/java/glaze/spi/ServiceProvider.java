package glaze.spi;

/**
 *
 */
public interface ServiceProvider<T>
{
   Class<T> serviceClass();

   T serviceImpl();
}
