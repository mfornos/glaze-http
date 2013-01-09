package glaze.examples.twitter.api;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class TwitterDateDeserializer extends JsonDeserializer<Date>
{
   private static final String LARGE_TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss Z yyyy";

   @Override
   public Date deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException
   {

      // TODO humanize cached format?
      SimpleDateFormat format = new SimpleDateFormat(LARGE_TWITTER_DATE_FORMAT, Locale.ENGLISH);
      try {
         return format.parse(jsonparser.getText());
      } catch (ParseException e) {
         throw new RuntimeException(e);
      }

   }
}
