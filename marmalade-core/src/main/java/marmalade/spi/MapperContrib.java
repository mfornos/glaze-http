package marmalade.spi;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 */
public interface MapperContrib
{
   String mimeType();

   ObjectMapper mapper();
}
