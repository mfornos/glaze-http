package marmalade.spi;

/**
 *
 */
public interface ServiceContrib
{
   Class<?> serviceClass();

   Object serviceImpl();
}
