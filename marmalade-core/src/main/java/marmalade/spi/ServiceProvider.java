package marmalade.spi;

/**
 *
 */
public interface ServiceProvider<T>
{
   Class<T> serviceClass();

   T serviceImpl();
}
