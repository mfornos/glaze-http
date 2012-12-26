package marmalade.test.http.replay;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import marmalade.test.http.ResponseBuilder;
import marmalade.test.http.SerializableResponse;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializationUtil
{

   private static final Logger LOGGER = LoggerFactory.getLogger(SerializationUtil.class);

   public static ResponseBuilder deserialize(ResponseBuilder response, String prefix)
   {
      populateResponse(response, (SerializableResponse) deserialize(prefix));
      return response;
   }

   public static void serialize(String prefix, HttpResponse response)
   {
      serialize(prefix, new SerializableResponse(response));
   }

   private static Object deserialize(String prefix)
   {
      try {
         InputStream file = new FileInputStream(prefix + ".response.ser");
         InputStream buffer = new BufferedInputStream(file);
         ObjectInput input = new ObjectInputStream(buffer);
         try {
            return input.readObject();
         } finally {
            input.close();
         }
      } catch (ClassNotFoundException ex) {
         LOGGER.error("Cannot perform input. Class not found.", ex);
      } catch (IOException ex) {
         LOGGER.error("Cannot perform input.", ex);
      }
      return null;
   }

   private static void populateResponse(ResponseBuilder response, SerializableResponse serializableResponse)
   {

      response.body(serializableResponse.asString());
      Map<String, String> headers = serializableResponse.getHeaders();
      for (Map.Entry<String, String> h : headers.entrySet()) {
         response.and(h.getKey(), h.getValue());
      }

   }

   private static void serialize(String prefix, Serializable ser)
   {
      try {
         OutputStream file = new FileOutputStream(prefix + ".response.ser");
         OutputStream buffer = new BufferedOutputStream(file);
         ObjectOutput output = new ObjectOutputStream(buffer);
         try {
            output.writeObject(ser);
         } finally {
            output.close();
         }
      } catch (IOException ex) {
         LOGGER.error("Cannot perform output.", ex);
      }
   }

}
