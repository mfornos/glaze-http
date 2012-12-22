package marmalade.spi;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 */
public interface MapperProvider
{
   String mimeType();

   ObjectMapper mapper();
}
